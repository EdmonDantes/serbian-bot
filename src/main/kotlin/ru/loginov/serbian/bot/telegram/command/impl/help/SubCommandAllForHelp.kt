//package ru.loginov.serbian.bot.telegram.command.impl.help
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.context.annotation.Lazy
//import org.springframework.stereotype.Component
//import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
//import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
//import ru.loginov.serbian.bot.telegram.command.manager.BotCommandManager
//import ru.loginov.telegram.api.util.StringBuilderMarkdownV2
//import ru.loginov.telegram.api.util.markdown2
//
//@Component
//class SubCommandAllForHelp : AbstractSubCommand() {
//
//    @Autowired
//    @Lazy
//    private lateinit var botCommandManager: BotCommandManager
//
//    override val commandName: String = "all"
//    override val simpleName: String = "Print all commands"
//    override val description: StringBuilderMarkdownV2 = markdown2 {
//        append("Print all commands and theirs descriptions")
//    }
//
//    override suspend fun execute(context: BotCommandExecuteContext) {
//        context.telegram.sendMessage {
//            this.chatId = context.chatId
//            buildText {
//                append("Bots commands:\n")
//                botCommandManager.getAllCommands().forEach { command ->
//                    append("\n")
//                    bold {
//                        append("/")
//                        append(command.commandName)
//                    }
//                    if (command.description != null) {
//                        append(" - ")
//                        append(description)
//                    }
//                    append("\n")
//                }
//            }
//        }
//    }
//}