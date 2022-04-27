package ru.loginov.serbian.bot.telegram.command

import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2
import java.util.concurrent.CancellationException
import kotlin.jvm.Throws

interface BotCommand {

    val commandName: String
    val simpleName: String?
    val description: StringBuilderMarkdownV2?
    val usage: StringBuilderMarkdownV2?
    val userAdditionalDataKeys: List<String>
    val isSubCommand: Boolean

    @Throws(CancellationException::class)
    suspend fun execute(context: BotCommandExecuteContext)

}