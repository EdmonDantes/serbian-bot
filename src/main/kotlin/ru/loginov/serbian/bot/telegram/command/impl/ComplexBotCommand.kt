package ru.loginov.serbian.bot.telegram.command.impl

import kotlinx.coroutines.CancellationException
import org.slf4j.LoggerFactory
import ru.loginov.serbian.bot.telegram.command.BotCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.util.markdown2
import ru.loginov.simple.permissions.annotation.ForcePermissionCheck
import ru.loginov.simple.permissions.annotation.IgnorePermissionCheck
import ru.loginov.simple.permissions.exception.AccessDeniedException
import ru.loginov.telegram.api.util.TelegramMessageTextBuilder

@ForcePermissionCheck
abstract class ComplexBotCommand : AbstractBotCommand() {

    private val LOGGER = LoggerFactory.getLogger(this.javaClass)
    private var _subCommands: Map<String, BotCommand>? = null

    val subCommands: Map<String, BotCommand>
        get() = _subCommands ?: emptyMap()

    open val canExecuteWithoutSubCommand: Boolean = false

    override fun getUsage(context: BotCommandExecuteContext): TelegramMessageTextBuilder? {
        return markdown2(context) {
            if (canExecuteWithoutSubCommand) {
                append("/$commandName\n")
            }

            val commands = subCommands.mapNotNull {
                try {
                    Triple(
                            it.value.getCommandName(context),
                            it.value.getDescription(context),
                            it.value.getUsage(context)
                    )
                } catch (e: AccessDeniedException) {
                    null
                }
            }

            if (commands.isNotEmpty()) {
                if (canExecuteWithoutSubCommand) {
                    bold {
                        append("@{phases.or}")
                    }
                    append('\n')
                }
                append("/$commandName <subcommand_name>")
                commands.forEach { (subCommandName, description, usage) ->
                    append('\n')
                    bold {
                        append("@{bot.complex.command.sub.command} '$subCommandName'")
                    }
                    append('\n')

                    if (description != null) {
                        append(description)
                        append('\n')
                    }
                    if (usage != null) {
                        italic {
                            append(usage)
                        }
                        append('\n')
                    }
                }
            } else if (!_subCommands.isNullOrEmpty()) {
                throw AccessDeniedException()
            }
        }
    }

    override suspend fun execute(context: BotCommandExecuteContext) {
        val menu = getSubCommandMenu(context)
        if (menu.isEmpty()) {
            if (canExecuteWithoutSubCommand) {
                try {
                    executeWithoutSubCommands()
                } catch (e: Exception) {
                    LOGGER.warn("Can not execute command '${this.commandName}'", e)
                    context.sendMessage {
                        markdown2(context) {
                            append("@{bot.complex.command.can.execute.command}")
                        }
                    }
                }
            } else {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.complex.command.can.not.find.subcommands}")
                    }
                }
            }
        } else {
            val commandName = context.getNextArgument(menu, "@{bot.complex.command.next.subcommand}")

            val command = subCommands[commandName]
            if (command != null) {
                try {
                    command.execute(context)
                } catch (e: Exception) {
                    if (e is CancellationException) {
                        LOGGER.debug("Subcommand with name '$commandName' in command '${this.commandName} was cancelled")
                        return
                    }

                    LOGGER.warn(
                            "Can not execute subcommand with name '$commandName' in command '${this.commandName}'",
                            e
                    )
                    context.sendMessage {
                        markdown2(context) {
                            append("@{bot.complex.command.can.not.execute.subcommand}. @{phases.internal.error}")
                        }
                    }
                }
            } else {
                LOGGER.error("Can not find subcommand with '$commandName'")
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.complex.command.can.not.execute.subcommand}. @{phases.internal.error}")
                    }
                }
            }
        }
    }

    private fun getSubCommandMenu(context: BotCommandExecuteContext): Map<String, String> =
            _subCommands?.values?.mapNotNull {
                try {
                    val commandName = it.getCommandName(context)
                    val shortDescription = it.getShortDescription(context)
                    (shortDescription ?: commandName) to commandName
                } catch (e: Exception) {
                    null
                }
            }?.toMap() ?: emptyMap()

    @IgnorePermissionCheck
    open fun executeWithoutSubCommands() {
        error("Can not find any subcommands for class: '${this.javaClass}'")
    }
}