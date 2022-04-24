package ru.loginov.serbian.bot.telegram.command

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.user.UserManager
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
    private lateinit var userManager: UserManager

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

        if (update.message!!.text!!.startsWith('/')) {
            val commandName = update.message!!.text.let { text ->
                text!!.substring(1, text.indexOf(' ').let { if (it < 0) text.length else it })
            }

            val command = botCommandManager.getCommandByName(commandName)

            if (command != null) {
                val userId = update.message!!.from!!.id
                val charId = update.message!!.chat.id
                val lang = update.message!!.from!!.languageTag
                val arguments = update.message!!.text!!.substring(commandName.length + 1).split(" ").filter { it.isNotEmpty() }
                executor.execute {
//                    TODO("Create BotCommandExecuteContext")
//                     TODO: new command execute
                    runBlocking {
                        command.execute(botCommandExecuteContextFactory.createContext(userId, charId, lang, arguments))
                    }
                }
            }
        } else {
            val userId = update.message!!.from!!.id
            val user = userManager.getUserWithData(userId, ADDITIONAL_DATA_KEYS)

            if (user == null) {
                LOGGER.warn("Can not find user with id = '$userId'. Update message will be skipped for this user: '$update'")
                return
            }

            val stage = user.additionalData[ADDITIONAL_DATA_COMMAND_STAGE_KEY]?.let {
                it.split(':').let {
                    if (it.size < 2) {
                        null
                    } else {
                        val stageNumber = it[1].toLongOrNull()
                        if (stageNumber == null) {
                            userManager.setAdditionalData(userId, it[0], null)
                            null
                        } else {
                            it[0] to stageNumber
                        }
                    }
                }
            }

            if (stage == null) {
                LOGGER.warn("Can not find stage for user with id '$userId'. Update message will be skipped for this user: '$update'")
                return
            }

            val (commandName, stageNumber) = stage
            val command = botCommandManager.getCommandByName(commandName)
            if (command == null) {
                LOGGER.warn("Can not find command with name '$commandName'. Update message will be skipped: '$update'")
                return
            }

            TODO("Create BotCommandExecuteContext")

            //TODO: command.executeStage(stageNumber, context)
        }
    }

    private fun processCallbackQuery(update: Update) {
        TODO("'processCallbackQuery' method is not yet implemented")
    }

    companion object {
        /**
         *  It is name command and index of stage (example: help:0)
         */
        private const val ADDITIONAL_DATA_COMMAND_STAGE_KEY = "COMMAND_STAGE"

        private val ADDITIONAL_DATA_KEYS = listOf(
                ADDITIONAL_DATA_COMMAND_STAGE_KEY
        )

        private val LOGGER = LoggerFactory.getLogger(CommandHandler::class.java)
    }
}