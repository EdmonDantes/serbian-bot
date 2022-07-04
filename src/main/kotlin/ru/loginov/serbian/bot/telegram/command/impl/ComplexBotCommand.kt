package ru.loginov.serbian.bot.telegram.command.impl

import kotlinx.coroutines.CancellationException
import org.slf4j.LoggerFactory
import ru.loginov.serbian.bot.telegram.command.BotCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.util.markdown2
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.impl.localizationKey
import ru.loginov.simple.localization.impl.localizationRequest
import ru.loginov.simple.localization.impl.singleRequest
import ru.loginov.simple.localization.impl.stringRequest
import ru.loginov.simple.permissions.annotation.ForcePermissionCheck
import ru.loginov.simple.permissions.annotation.IgnorePermissionCheck

@ForcePermissionCheck
abstract class ComplexBotCommand : AbstractBotCommand() {

    private val LOGGER = LoggerFactory.getLogger(this.javaClass)
    private var _subCommands: Map<String, BotCommand>? = null

    val subCommands: Map<String, BotCommand>
        get() = _subCommands ?: emptyMap()

    open val canExecuteWithoutSubCommand: Boolean = false

    override suspend fun execute(context: BotCommandExecuteContext) {
        val menu = getSubCommandMenu(context)
        if (menu.isEmpty()) {
            if (canExecuteWithoutSubCommand) {
                try {
                    executeWithoutSubCommands(context)
                } catch (e: Exception) {
                    LOGGER.warn("Can not execute command '${this.commandName}'", e)
                    context.sendMessage {
                        markdown2(context.localization) {
                            append(localizationKey("bot.complex.command.can.execute.command"))
                        }
                    }
                }
            } else {
                context.sendMessage {
                    markdown2(context.localization) {
                        append(localizationKey("bot.complex.command.can.not.find.subcommands"))
                    }
                }
            }
        } else {
            val commandName = context.arguments.argument(
                    "commandName",
                    menu,
                    singleRequest("bot.complex.command.next.subcommand")
            )
                    .requiredAndGet()

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
                        markdown2(context.localization) {
                            append(localizationRequest {
                                add("bot.complex.command.can.not.execute.subcommand")
                                withoutLocalization(". ")
                                add("phases.internal.error")
                            })
                        }
                    }
                }
            } else {
                LOGGER.error("Can not find subcommand with '$commandName'")
                context.sendMessage {
                    markdown2(context.localization) {
                        append(localizationRequest {
                            add("bot.complex.command.can.not.execute.subcommand")
                            withoutLocalization(". ")
                            add("phases.internal.error")
                        })
                    }
                }
            }
        }
    }

    private fun getSubCommandMenu(context: BotCommandExecuteContext): Map<LocalizationRequest, String> =
            _subCommands?.values?.mapNotNull {
                try {
                    val commandName = it.getCommandName(context)
                    val actionDescription = it.getActionDescription(context)
                    (actionDescription?.let { singleRequest(it) } ?: stringRequest(commandName)) to commandName
                } catch (e: Exception) {
                    null
                }
            }?.toMap() ?: emptyMap()

    @IgnorePermissionCheck
    open suspend fun executeWithoutSubCommands(context: BotCommandExecuteContext) {
        error("Can not find any subcommands for class: '${this.javaClass}'")
    }
}