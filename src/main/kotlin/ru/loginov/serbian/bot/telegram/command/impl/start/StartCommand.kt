package ru.loginov.serbian.bot.telegram.command.impl.start

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.user.UserManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.withLocalization
import ru.loginov.serbian.bot.telegram.command.argument.optionalAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractBotCommand

@Component
class StartCommand(
        private val userManager: UserManager
) : AbstractBotCommand() {
    override val commandName: String = "start"

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.withLocalization("bot.command.start._argument") {
            val lang = context
                    .argument("lang", "lang")
                    .optionalAndGet()
                    ?: return

            try {
                userManager.update(context.user.id!!, language = lang)
            } catch (e: Exception) {
                LOGGER.warn("Can not update language to '$lang' for user with id '${context.user.id}'", e)
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StartCommand::class.java)
    }
}