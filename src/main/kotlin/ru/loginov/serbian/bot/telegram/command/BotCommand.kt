package ru.loginov.serbian.bot.telegram.command

import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.simple.permissions.annotation.ForcePermissionCheck
import ru.loginov.simple.permissions.exception.AccessDeniedException
import ru.loginov.telegram.api.util.Markdown2StringBuilder
import java.util.concurrent.CancellationException

@ForcePermissionCheck
interface BotCommand {

    /**
     * Please only for internal usage (example: logs). Not for users
     */
    val commandName: String

    @ForcePermissionCheck
    @Throws(AccessDeniedException::class)
    fun getCommandName(context: BotCommandExecuteContext): String = commandName

    @ForcePermissionCheck
    @Throws(AccessDeniedException::class)
    fun getShortDescription(context: BotCommandExecuteContext): String? = null

    @ForcePermissionCheck
    @Throws(AccessDeniedException::class)
    fun getDescription(context: BotCommandExecuteContext): Markdown2StringBuilder? = null

    @ForcePermissionCheck
    @Throws(AccessDeniedException::class)
    fun getUsage(context: BotCommandExecuteContext): Markdown2StringBuilder? = null

    @ForcePermissionCheck
    @Throws(CancellationException::class, AccessDeniedException::class)
    suspend fun execute(context: BotCommandExecuteContext)
}