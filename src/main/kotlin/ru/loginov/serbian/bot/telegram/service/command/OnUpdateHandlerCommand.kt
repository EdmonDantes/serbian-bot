package ru.loginov.serbian.bot.telegram.service.command

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.entity.Update
import ru.loginov.serbian.bot.telegram.service.command.context.BotCommandExecuteContextFactory
import ru.loginov.serbian.bot.telegram.service.command.manager.BotCommandManager
import ru.loginov.serbian.bot.telegram.service.update.OnUpdateHandler
import java.util.concurrent.Executor

@Component
class OnUpdateHandlerCommand : OnUpdateHandler {

    @Autowired
    private lateinit var botCommandManager: BotCommandManager

    @Autowired
    private lateinit var botCommandExecuteContextFactory: BotCommandExecuteContextFactory

    @Autowired
    @Qualifier("smalltasks")
    private lateinit var executor: Executor

    override fun onUpdate(update: Update) {
        if (update.message?.text != null && update.message.from != null && update.message.text.startsWith("/")) {

            val commandName = update.message.text.let { text ->
                text.substring(1, text.indexOf(' ').let { if (it < 0) text.length else it })
            }

            val command = botCommandManager.getCommandByName(commandName)

            if (command != null) {
                val userId = update.message.from.id
                val charId = update.message.chat.id
                val lang = update.message.from.languageTag ?: "en"
                val arguments = update.message.text.substring(commandName.length + 1).split(" ").filter { it.isNotEmpty() }
                executor.execute {
                    runBlocking {
                        command.execute(botCommandExecuteContextFactory.createContext(userId, charId, lang, arguments))
                    }
                }
            }
        }
    }
}