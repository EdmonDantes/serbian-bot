package ru.loginov.serbian.bot.telegram.command.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.BotCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.manager.BotCommandManager
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2
import ru.loginov.telegram.api.util.markdown2

@Component
class HelpCommand : BotCommand {

    @Autowired
    @Lazy
    private lateinit var botCommandManager: BotCommandManager

    override val commandName: String = "help"
    override val description: StringBuilderMarkdownV2 =
            StringBuilderMarkdownV2.fromString("Print all bots commands and how to use it")
    override val usage: StringBuilderMarkdownV2 = markdown2 {
        append("Short describe all commands:\n/$commandName\nUsage of command\n/$commandName ")
        italic {
            append("command_name")
        }
    }

    override suspend fun execute(context: BotCommandExecuteContext) {

        context.telegramApi.sendMessage {
            this.chatId = context.chatId
            buildText {
                if (context.arguments.isEmpty()) {
                    printAllCommand()
                } else {
                    printCommandUsage(context.arguments[0])
                }
            }

        }
    }

    private fun StringBuilderMarkdownV2.printAllCommand() {
        append("Bots commands:\n")
        botCommandManager.getAllCommands().forEach { command ->
            append("\n")
            bold {
                append("/")
                append(command.commandName)
            }
            if (command.description != null) {
                append(" - ")
                append(description)
            }
            append("\n")
        }
    }

    private fun StringBuilderMarkdownV2.printCommandUsage(commandName: String) {
        val command = botCommandManager.getCommandByName(commandName)
        if (command == null) {
            append("Can not find command with name '$commandName'")
        } else if (command.usage == null) {
            append("Command /${command.commandName} have no special instructions for usage")
        } else {
            append("Usage for command /${command.commandName}\n")
            append(command.usage!!)
        }
    }
}