package ru.loginov.serbian.bot.telegram.command.context

import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.telegram.command.context.arguments.BotCommandArgumentManager
import ru.loginov.telegram.api.TelegramAPI

interface BotCommandExecuteContext : TelegramAPI {


    val telegram: TelegramAPI
    val user: UserDto
    val chatId: Long
    val argumentManager: BotCommandArgumentManager

//    fun startStage(stage: Long)
//    fun endStage(stage: Long)
//
//    fun <T> stage(stage: Long, block: () -> T)
}