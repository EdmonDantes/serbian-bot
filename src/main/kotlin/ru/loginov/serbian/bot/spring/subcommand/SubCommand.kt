package ru.loginov.serbian.bot.spring.subcommand

import ru.loginov.serbian.bot.telegram.command.BotCommand
import kotlin.reflect.KClass

@Target(allowedTargets = [AnnotationTarget.CLASS])
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@MustBeDocumented
annotation class SubCommand(
        val parent: KClass<out BotCommand>,
        val subCommandName: String
)
