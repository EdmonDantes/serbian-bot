//package ru.loginov.serbian.bot.telegram.command.impl.help
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Component
//import ru.loginov.serbian.bot.telegram.command.BotCommand
//import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
//import ru.loginov.serbian.bot.telegram.command.impl.AbstractBotCommandWithSubCommands
//import ru.loginov.telegram.api.util.StringBuilderMarkdownV2
//
//@Component
//class HelpCommand : AbstractBotCommandWithSubCommands() {
//
//    override lateinit var subCommands: List<BotCommand>
//
//    override val commandName: String = "help"
//    override val description: StringBuilderMarkdownV2 =
//            StringBuilderMarkdownV2.fromString("Print all bots commands and how to use it")
//
//    @Autowired
//    fun initSubCommands(command0: SubCommandAllForHelp, command1: SubCommandHelpForHelp) {
//        subCommands = listOf(command0, command1)
//    }
//}