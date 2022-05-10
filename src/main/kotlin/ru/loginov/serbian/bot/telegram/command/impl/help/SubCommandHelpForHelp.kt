package ru.loginov.serbian.bot.telegram.command.impl.help

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.permission.exception.HaveNotPermissionException
import ru.loginov.serbian.bot.spring.subcommand.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.telegram.command.manager.BotCommandManager
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2
import ru.loginov.telegram.api.util.markdown2

@Component
@SubCommand(parents = [HelpCommand::class])
class SubCommandHelpForHelp : AbstractSubCommand() {

    @Autowired
    @Lazy
    private lateinit var botCommandManager: BotCommandManager

    override val commandName: String = "help"
    override val shortDescription: String = "Print usage for command"
    override val description: StringBuilderMarkdownV2 = markdown2 {
        append("Print usage for command")
    }

    override suspend fun execute(context: BotCommandExecuteContext) {
        val commandName = context.argumentManager.getNextArgument("command name")

        if (commandName != null) {
            context.telegram.sendMessage {
                this.chatId = context.chatId
                buildText {
                    val command = botCommandManager.getCommandByName(commandName)
                    if (command == null) {
                        append("Can not find command with name '$commandName'")
                    } else {
                        try {
                            val (commandName, usage) = command.getCommandName(context) to command.getUsage(context)

                            if (usage == null) {
                                append("Command /${commandName} have no special instructions for usage")
                            } else {
                                append("Usage for command /${commandName}:\n")
                                append(usage)
                            }
                        } catch (e: HaveNotPermissionException) {
                            append("Can not find command with name '$commandName'")
                        }
                    }
                }
            }
        }
    }
}