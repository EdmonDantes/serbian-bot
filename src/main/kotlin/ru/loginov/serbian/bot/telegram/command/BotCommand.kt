package ru.loginov.serbian.bot.telegram.command

import ru.loginov.serbian.bot.spring.permission.exception.HaveNotPermissionException
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.telegram.api.util.StringBuilderMarkdownV2
import java.util.concurrent.CancellationException
import kotlin.jvm.Throws

interface BotCommand {

    /**
     * Please only for internal usage (example: logs). Not for users
     */
    val commandName: String
    /**
     * Please only for internal usage (example: logs). Not for users
     */
    val shortDescription: String?
    /**
     * Please only for internal usage (example: logs). Not for users
     */
    val description: StringBuilderMarkdownV2?
    /**
     * Please only for internal usage (example: logs). Not for users
     */
    val usage: StringBuilderMarkdownV2?

    @Throws(HaveNotPermissionException::class)
    fun getCommandName(context: BotCommandExecuteContext) : String = commandName

    @Throws(HaveNotPermissionException::class)
    fun getShortDescription(context: BotCommandExecuteContext) : String? = shortDescription

    @Throws(HaveNotPermissionException::class)
    fun getDescription(context: BotCommandExecuteContext) : StringBuilderMarkdownV2? = description

    @Throws(HaveNotPermissionException::class)
    fun getUsage(context: BotCommandExecuteContext) : StringBuilderMarkdownV2? = usage

    @Throws(CancellationException::class, HaveNotPermissionException::class)
    suspend fun execute(context: BotCommandExecuteContext)
}