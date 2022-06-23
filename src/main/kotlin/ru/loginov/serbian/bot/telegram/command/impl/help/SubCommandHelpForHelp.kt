package ru.loginov.serbian.bot.telegram.command.impl.help

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.withLocalization
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.telegram.command.manager.BotCommandManager
import ru.loginov.serbian.bot.util.markdown2
import ru.loginov.simple.permissions.exception.AccessDeniedException

@Component
@SubCommand(parents = [HelpCommand::class])
class SubCommandHelpForHelp : AbstractSubCommand() {

    @Autowired
    @Lazy
    private lateinit var botCommandManager: BotCommandManager

    override val commandName: String = "help"
    override val actionDescription: String = "@{bot.command.help.help._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.withLocalization("bot.command.help.help._argument") {
            val commandName = argument("commandName", "commandName").requiredAndGet()

            context.sendMessage {
                markdown2(context) {
                    val command = botCommandManager.getCommandByName(commandName)
                    if (command == null || command.commandName == "start") {
                        append("@{bot.command.help.help._.can.not.find.command} '$commandName'")
                    } else {
                        try {
                            val (commandName, usage) = command.getCommandName(context) to command.getActionDescription(
                                    context
                            )

                            if (usage == null) {
                                append("@{bot.command.help.help._.command.have.not.special.usage}{${commandName}}")
                            } else {
                                append("@{bot.command.help.help._.usage.for.command} /${commandName}:\n")
                                append(usage)
                            }
                        } catch (e: AccessDeniedException) {
                            append("@{bot.command.help.help._.can.not.find.command} '$commandName'")
                        }
                    }
                }
            }
        }
    }
}