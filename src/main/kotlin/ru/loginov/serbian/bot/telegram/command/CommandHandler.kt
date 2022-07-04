package ru.loginov.serbian.bot.telegram.command

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.callback.CallbackData
import ru.loginov.serbian.bot.telegram.callback.CallbackExecutor
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContextFactory
import ru.loginov.serbian.bot.telegram.command.manager.BotCommandManager
import ru.loginov.serbian.bot.telegram.update.OnUpdateHandler
import ru.loginov.simple.localization.manager.LocalizationManager
import ru.loginov.simple.permissions.exception.AccessDeniedException
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.Update
import java.time.LocalDateTime
import java.util.concurrent.CancellationException
import javax.annotation.PostConstruct
import kotlin.streams.toList

@Component
class CommandHandler(
        private val commandManager: BotCommandManager,
        private val localizationManager: LocalizationManager,
        private val commandContextFactory: BotCommandExecuteContextFactory,
        private val callbackExecutor: CallbackExecutor,
        private val telegram: TelegramAPI,
        private val dispatcher: CoroutineDispatcher
) : OnUpdateHandler {

    @PostConstruct
    fun postConstruct() {
        runBlocking {
            localizationManager.supportLanguages.map {
                val emptyContext = commandContextFactory.createEmptyContext(it)
                it to commandManager.getAllCommands().mapNotNull {
                    try {
                        it.getCommandName(emptyContext) to it.getActionDescription(emptyContext)
                    } catch (e: Exception) {
                        null
                    }
                }
            }.forEach { (lang, commands) ->
                telegram.setMyCommands {
                    this.commands = commands
                            .stream()
                            .limit(100)
                            .map { ru.loginov.telegram.api.entity.BotCommand(it.first, it.second ?: "-") }
                            .toList()
                    this.language = lang
                }
            }
        }
    }

    override suspend fun onUpdate(update: Update) {
        coroutineScope {
            LOGGER.trace("Start to execute onUpdate: ${LocalDateTime.now()}")
            val first = update.message?.let { async(dispatcher) { processUpdateMessage(update) } }
            val second = update.callbackQuery?.let { async(dispatcher) { processCallbackQuery(update) } }
            LOGGER.trace("Started coroutines in onUpdate: ${LocalDateTime.now()}")

            second?.await()
            first?.await()

            LOGGER.trace("Ended execute onUpdate: ${LocalDateTime.now()}")
        }
    }

    private suspend fun processUpdateMessage(update: Update) {
        if (update.message?.from == null) {
            LOGGER.warn("Can not process update message, because message haven't sender: '$update'")
            return
        }

        if (update.message!!.from!!.isBot) {
            LOGGER.warn("Can not process update message, because message's sender is a bot: '$update'")
            telegram.sendMessage {
                chatId = update.message!!.chat.id
                markdown2 {
                    append("This bot doesn't support messages from another bots")
                }
            }
            return
        }

        val userId = update.message!!.from!!.id
        val chatId = update.message!!.chat.id
        val lang = update.message!!.from!!.languageTag

        if (!update.message!!.text.startsWith('/')) {
            val data = CallbackData(dataFromMessage = update.message?.text, location = update.message?.location)
            callbackExecutor.invoke(chatId, userId, data)
            return
        }

        val commandName = update.message!!.text.let { text ->
            text.substring(1, text.indexOf(' ').let { if (it < 0) text.length else it })
        }

        val command = commandManager.getCommandByName(commandName)
        if (command == null) {
            LOGGER.debug("Can not find command with name '$commandName' for chat '$chatId' and user '$userId'")
            printCanNotFindCommand(chatId, commandName)
            return
        }

        val argumentsStr = update.message!!.text.substring(commandName.length + 1)

        // Cancel callback, because we should execute another command, so we should cancel current command
        callbackExecutor.cancel(chatId, userId)

        val context = commandContextFactory.createContext(userId, chatId, update.message!!, argumentsStr, lang)
        try {
            command.execute(context)
        } catch (e: Exception) {
            processFailedReason(context, command, e)
        }
    }

    private suspend fun processCallbackQuery(update: Update) {
        telegram.answerCallbackQuery {
            callbackQueryId = update.callbackQuery!!.id
        }

        if (update.callbackQuery?.data == null) {
            LOGGER.warn("Can not process callback. Callback query hasn't 'data' field. Update: '$update'")
            return
        }

        val (ids, dataStr) = update.callbackQuery!!.data!!.split("#").filter { it.isNotEmpty() }.let {
            (if (it.isNotEmpty()) it[0] else null) to (if (it.size > 1) it[1] else null)
        }

        if (ids == null) {
            LOGGER.warn("Can not process callback. Can not find identification data from callback data. Update: '$update'")
            return
        }

        val (chatId, userId) = ids.split(":").mapNotNull { it.toLongOrNull() }.let {
            (if (it.isNotEmpty()) it[0] else null) to (if (it.size > 1) it[1] else null)
        }

        if (chatId == null) {
            LOGGER.warn("Can not process callback. Can not find chats' id. Update: '$update'")
            return
        }

        callbackExecutor.invoke(chatId, userId, CallbackData(dataFromCallback = dataStr))
    }


    private suspend fun processFailedReason(context: BotCommandExecuteContext, command: BotCommand, e: Exception) {
        when (e) {
            is AccessDeniedException -> {
                LOGGER.info("User '${context.user}' can not have enough permissions for execute command with name '${command.commandName}'")
                printCanNotFindCommand(context.chatId, command.commandName)
            }
            is CancellationException ->
                LOGGER.info("Command with name '${command.commandName}' was canceled")
            else ->
                LOGGER.error("Can not execute bot command with name '${command.commandName}'", e)
        }
    }

    private suspend fun printCanNotFindCommand(chatId: Long, commandName: String) {
        telegram.sendMessage {
            this.chatId = chatId
            markdown2 {
                append("Can not find command with name '$commandName'")
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CommandHandler::class.java)
    }
}