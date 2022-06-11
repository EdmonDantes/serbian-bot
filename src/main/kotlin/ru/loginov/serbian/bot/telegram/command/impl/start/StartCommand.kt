package ru.loginov.serbian.bot.telegram.command.impl.start

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractBotCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
class StartCommand : AbstractBotCommand() {
    override val commandName: String = "start"

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.sendMessage {
            markdown2(context) {
                append("@{bot.command.start._.welcome}")
            }
        }
    }
}