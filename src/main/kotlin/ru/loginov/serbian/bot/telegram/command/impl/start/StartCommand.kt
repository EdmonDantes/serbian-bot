package ru.loginov.serbian.bot.telegram.command.impl.start

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.user.UserManager
import ru.loginov.serbian.bot.telegram.command.argument.optionalAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.LocalizedBotCommand

@Component
class StartCommand(
        private val userManager: UserManager
) : LocalizedBotCommand("bot.command.start") {
    override val commandName: String = "start"

    override suspend fun BotCommandExecuteContext.action() {
        val lang = arguments.language("lang").optionalAndGet() ?: return

        try {
            userManager.update(user.id!!, language = lang)
        } catch (e: Exception) {
            LOGGER.warn("Can not update language to '$lang' for user with id '${user.id}'", e)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StartCommand::class.java)
    }
}