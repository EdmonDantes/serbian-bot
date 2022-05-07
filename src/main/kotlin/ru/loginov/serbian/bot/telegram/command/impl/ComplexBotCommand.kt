package ru.loginov.serbian.bot.telegram.command.impl

import ru.loginov.serbian.bot.spring.permission.annotation.IgnorePermissionCheck
import ru.loginov.serbian.bot.spring.permission.annotation.PermissionCheck
import ru.loginov.serbian.bot.spring.permission.exception.HaveNotPermissionException
import ru.loginov.serbian.bot.telegram.command.BotCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2
import ru.loginov.telegram.api.util.markdown2

@PermissionCheck
abstract class ComplexBotCommand : AbstractBotCommand() {

    private var _subCommands: Map<String, BotCommand>? = null
        set(value) {
            field = value
            _subCommandsNames = value?.keys?.toList() ?: emptyList()
        }
    private var _subCommandsNames: List<String> = emptyList()

    val subCommands: Map<String, BotCommand>
        get() = _subCommands ?: emptyMap()

    open val canExecuteWithoutSubCommand: Boolean = false

    override fun getUsage(context: BotCommandExecuteContext): StringBuilderMarkdownV2? {
        return markdown2 {
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
                } catch (e: HaveNotPermissionException) {
                    null
                }
            }

            if (commands.isNotEmpty()) {
                if (canExecuteWithoutSubCommand) {
                    bold {
                        append("or")
                    }
                    append('\n')
                }
                append("/$commandName <subcommand_name>")
                commands.forEach { (subCommandName, description, usage) ->
                    append('\n')
                    bold {
                        append("Sub command '$subCommandName'")
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
            }
        }
    }

    override suspend fun execute(context: BotCommandExecuteContext) {
        if (_subCommandsNames.isEmpty()) {
            executeWithoutSubCommands()
        } else {
            val commandName = context.argumentManager.getNextArgument(_subCommandsNames, "subcommand")
            subCommands[commandName]?.execute(context) ?: context.sendMessage {
                buildText {
                    append("Can not find subcommand with $commandName")
                }
            }
        }
    }

    @IgnorePermissionCheck
    open fun executeWithoutSubCommands() {}
}