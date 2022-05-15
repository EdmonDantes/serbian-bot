package ru.loginov.serbian.bot.spring.subcommand.annotation

import ru.loginov.serbian.bot.telegram.command.BotCommand
import kotlin.reflect.KClass

@Target(allowedTargets = [AnnotationTarget.CLASS])
@Retention(AnnotationRetention.RUNTIME)
@Repeatable //FIXME: Add support (sometime in future)
@MustBeDocumented
annotation class SubCommand(
        val parents: Array<KClass<out BotCommand>>
)
