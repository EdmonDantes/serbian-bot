package ru.loginov.serbian.bot.telegram.command.impl.help

import io.github.edmondantes.simple.localization.impl.localizationKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.base.LocalizedSubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.manager.BotCommandManager
import ru.loginov.serbian.bot.telegram.util.markdown2
import ru.loginov.simple.permissions.exception.AccessDeniedException

@Component
@SubCommand(parents = [HelpCommand::class])
class SubCommandHelpForHelp : LocalizedSubCommand("bot.command.help.help") {

    @Autowired
    @Lazy
    private lateinit var botCommandManager: BotCommandManager

    override val commandName: String = "help"

    override suspend fun BotCommandExecuteContext.action() {
        val commandName = arguments.argument("commandName").requiredAndGet()
        sendMessage {
            markdown2(localization) {
                val command = botCommandManager.getCommandByName(commandName)
                if (command == null || command.commandName == "start") {
                    append(localizationKey("_.can.not.find.command"))
                    append(" '$commandName'")
                } else {
                    try {
                        val (commandName, usage) =
                                command.getCommandName(this@action) to command.getActionDescription(this@action)

                        if (usage == null) {
                            append(localizationKey("_.command.have.not.special.usage", commandName))
                        } else {
                            append(localizationKey("_.usage.for.command"))
                            append(" /${commandName}:\n")
                            append(usage)
                        }
                    } catch (e: AccessDeniedException) {
                        append(localizationKey("_.can.not.find.command"))
                        append(" '$commandName'")
                    }
                }
            }
        }
    }
}