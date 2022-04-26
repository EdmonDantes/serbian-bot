package ru.loginov.serbian.bot.telegram.command

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.callback.CallbackExecutor
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContextFactory
import ru.loginov.serbian.bot.telegram.command.manager.BotCommandManager
import ru.loginov.serbian.bot.telegram.update.OnUpdateHandler
import ru.loginov.telegram.api.TelegramAPI
import ru.loginov.telegram.api.entity.Update
import java.util.concurrent.Executor

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

    @Autowired
    @Qualifier("small_tasks")
    private lateinit var executor: Executor

    override fun onUpdate(update: Update) {
        if (update.message != null) {
            processUpdateMessage(update)
        }

        if (update.callbackQuery != null) {
            processCallbackQuery(update)
        }
    }

    private fun processUpdateMessage(update: Update) {
        if (update.message?.from == null || update.message?.text == null) {
            LOGGER.warn("Can not process update message, because message haven't sender or/and text: '$update'")
            return
        }

        if (update.message!!.from!!.isBot) {
            LOGGER.warn("Can not process update message, because message's sender is a bot: '$update'")
            runBlocking {
                telegram.sendMessage {
                    chatId = update.message!!.chat.id
                    buildText {
                        append("This bot doesn't support messages from bots")
                    }
                }
            }
            return
        }

        val userId = update.message!!.from!!.id
        val charId = update.message!!.chat.id
        val lang = update.message!!.from!!.languageTag

        if (update.message!!.text!!.startsWith('/')) {
            val commandName = update.message!!.text.let { text ->
                text!!.substring(1, text.indexOf(' ').let { if (it < 0) text.length else it })
            }

            val command = botCommandManager.getCommandByName(commandName)

            if (command != null) {
                val argumentsStr = update.message!!.text!!.substring(commandName.length + 1)

                executor.execute {
                    callbackExecutor.cancel(charId, userId)
                }

                executor.execute {
                    runBlocking {
                        command.execute(botCommandExecuteContextFactory.createContext(userId, charId, lang, argumentsStr))
                    }
                }
                return
            }
        }

        callbackExecutor.invoke(charId, userId, update.message?.text)
    }

    private fun processCallbackQuery(update: Update) {
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

        executor.execute {
            callbackExecutor.invoke(chatId, userId, dataStr)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CommandHandler::class.java)
    }
}