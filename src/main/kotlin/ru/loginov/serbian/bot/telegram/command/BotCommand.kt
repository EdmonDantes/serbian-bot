package ru.loginov.serbian.bot.telegram.command

import ru.loginov.serbian.bot.spring.permission.annotation.PermissionCheck
import ru.loginov.serbian.bot.spring.permission.exception.HaveNotPermissionException
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.telegram.api.util.TelegramMessageTextBuilder
import java.util.concurrent.CancellationException

@PermissionCheck
interface BotCommand {

    /**
     * Please only for internal usage (example: logs). Not for users
     */
    val commandName: String

    @PermissionCheck
    @Throws(HaveNotPermissionException::class)
    fun getCommandName(context: BotCommandExecuteContext) : String = commandName

    @PermissionCheck
    @Throws(HaveNotPermissionException::class)
    fun getShortDescription(context: BotCommandExecuteContext): String? = null

    @PermissionCheck
    @Throws(HaveNotPermissionException::class)
    fun getDescription(context: BotCommandExecuteContext): TelegramMessageTextBuilder? = null

    @PermissionCheck
    @Throws(HaveNotPermissionException::class)
    fun getUsage(context: BotCommandExecuteContext): TelegramMessageTextBuilder? = null

    @PermissionCheck
    @Throws(CancellationException::class, HaveNotPermissionException::class)
    suspend fun execute(context: BotCommandExecuteContext)
}