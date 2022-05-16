package ru.loginov.serbian.bot.spring.subcommand

import ru.loginov.serbian.bot.telegram.command.BotCommand
import kotlin.reflect.KClass

interface CommandRegister {

    fun registerBean(bean: BotCommand, beanName: String)
    fun registerNewSubCommand(bean: BotCommand, beanName: String, parent: KClass<*>)
    fun injectBean(beanName: String, bean: BotCommand)
    fun injectSubCommands(beanName: String)

}