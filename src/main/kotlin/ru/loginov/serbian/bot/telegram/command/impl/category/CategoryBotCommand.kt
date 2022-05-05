//package ru.loginov.serbian.bot.telegram.command.impl.category
//
//import org.springframework.stereotype.Component
//import ru.loginov.serbian.bot.telegram.command.BotCommand
//import ru.loginov.serbian.bot.telegram.command.impl.AbstractBotCommandWithSubCommands
//import ru.loginov.telegram.api.util.StringBuilderMarkdownV2
//import ru.loginov.telegram.api.util.markdown2
//
//@Component
//class CategoryBotCommand : AbstractBotCommandWithSubCommands() {
//
//    override lateinit var subCommands: List<BotCommand>
//    override val commandName: String = "category"
//    override val description: StringBuilderMarkdownV2? = markdown2 {
//        append("Work with categories")
//    }
//}