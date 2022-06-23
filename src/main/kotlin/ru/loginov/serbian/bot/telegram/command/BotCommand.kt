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

    /**
     * Try to get command name with current permissions
     */
    @ForcePermissionCheck
    @Throws(AccessDeniedException::class)
    fun getCommandName(context: BotCommandExecuteContext): String = commandName

    /**
     * Try to get command action description.
     *
     * Used for short description in command menu
     */
    @ForcePermissionCheck
    @Throws(AccessDeniedException::class)
    fun getActionDescription(context: BotCommandExecuteContext): String?

    /**
     * Try to get full description
     */
    @ForcePermissionCheck
    @Throws(AccessDeniedException::class)
    fun getDescription(context: BotCommandExecuteContext): Markdown2StringBuilder? = null

    /**
     * Try to execute command
     */
    @ForcePermissionCheck
    @Throws(CancellationException::class, AccessDeniedException::class)
    suspend fun execute(context: BotCommandExecuteContext)
}