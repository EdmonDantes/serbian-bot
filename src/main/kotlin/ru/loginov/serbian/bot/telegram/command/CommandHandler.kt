package ru.loginov.serbian.bot.telegram.command

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.permission.exception.HaveNotPermissionException
import ru.loginov.serbian.bot.telegram.callback.CallbackData
import ru.loginov.serbian.bot.telegram.callback.CallbackExecutor
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContextFactory
import ru.loginov.serbian.bot.telegram.command.manager.BotCommandManager
import ru.loginov.serbian.bot.telegram.update.OnUpdateHandler
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.Update
import java.util.concurrent.CancellationException

@Component
class CommandHandler : OnUpdateHandler {

    @Autowired
    private lateinit var botCommandManager: BotCommandManager

    @Autowired
    private lateinit var botCommandExecuteContextFactory: BotCommandExecuteContextFactory

    @Autowired
    private lateinit var telegram: TelegramAPI

    @Autowired
    private lateinit var callbackExecutor: CallbackExecutor

    override suspend fun onUpdate(update: Update) {
        coroutineScope {
            val first = update.message?.let { async { processUpdateMessage(update) } }
            val second = update.callbackQuery?.let { async { processCallbackQuery(update) } }

            second?.await()
            first?.await()
        }
    }

    private suspend fun processUpdateMessage(update: Update) {
        if (update.message?.from == null || update.message?.text == null) {
            LOGGER.warn("Can not process update message, because message haven't sender or/and text: '$update'")
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

        if (!update.message!!.text!!.startsWith('/')) {
            callbackExecutor.invoke(chatId, userId, CallbackData(update.message?.text, null))
            return
        }

        val commandName = update.message!!.text.let { text ->
            text!!.substring(1, text.indexOf(' ').let { if (it < 0) text.length else it })
        }

        val command = botCommandManager.getCommandByName(commandName)
        if (command == null) {
            LOGGER.debug("Can not find command with name '$commandName' for chat '$chatId' and user '$userId'")
            printCanNotFindCommand(chatId, commandName)
        }

        val argumentsStr = update.message!!.text!!.substring(commandName.length + 1)

        // Cancel callback, because we should execute another command, so we should cancel current command
        callbackExecutor.cancel(chatId, userId)

        val context = botCommandExecuteContextFactory.createContext(userId, chatId, lang, argumentsStr)
        try {
            command!!.execute(context)
        } catch (e: Exception) {
            processFailedReason(context, command!!, e)
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
            (if (it.size > 0) it[0] else null) to (if (it.size > 1) it[1] else null)
        }

        if (ids == null) {
            LOGGER.warn("Can not process callback. Can not find identification data from callback data. Update: '$update'")
            return
        }

        val (chatId, userId) = ids.split(":").mapNotNull { it.toLongOrNull() }.let {
            (if (it.size > 0) it[0] else null) to (if (it.size > 1) it[1] else null)
        }

        if (chatId == null) {
            LOGGER.warn("Can not process callback. Can not find chats' id. Update: '$update'")
            return
        }

        callbackExecutor.invoke(chatId, userId, CallbackData(null, dataStr))
    }


    private suspend fun processFailedReason(context: BotCommandExecuteContext, command: BotCommand, e: Exception) {
        when (e) {
            is HaveNotPermissionException -> {
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