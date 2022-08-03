package ru.loginov.serbian.bot.telegram.command.impl.help

import io.github.edmondantes.simple.localization.impl.localizationKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.base.AbstractSubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.manager.BotCommandManager
import ru.loginov.serbian.bot.telegram.util.markdown2
import ru.loginov.simple.permissions.exception.AccessDeniedException

@Component
@SubCommand(parents = [HelpCommand::class])
class SubCommandAllForHelp : AbstractSubCommand() {

    @Autowired
    @Lazy
    private lateinit var botCommandManager: BotCommandManager

    override val commandName: String = "all"

    override fun getActionDescription(context: BotCommandExecuteContext): String? =
            context.localization.localizeOrNull(ACTION_DESCRIPTION_LOCATION_KEY)

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.sendMessage {
            markdown2(context.localization) {
                append(localizationKey("bot.command.help.all._.bots.commands"))
                append(":\n")
                botCommandManager.getAllCommands().filter { it.commandName != "start" }.mapNotNull {
                    try {
                        it.getCommandName(context) to it.getDescription(context)
                    } catch (e: AccessDeniedException) {
                        null
                    }
                }.forEach { (commandName, description) ->
                    append("\n")
                    bold {
                        append("/")
                        append(commandName)
                    }
                    if (description != null) {
                        append(" - ")
                        append(description)
                    }
                    append("\n")
                }
            }
        }
    }

    companion object {
        private val ACTION_DESCRIPTION_LOCATION_KEY = localizationKey("bot.command.help.all._actionDescription")
    }
}