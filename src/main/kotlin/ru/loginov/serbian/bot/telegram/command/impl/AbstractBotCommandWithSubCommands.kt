//package ru.loginov.serbian.bot.telegram.command.impl
//
//import ru.loginov.serbian.bot.telegram.command.BotCommand
//import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
//import ru.loginov.telegram.api.util.StringBuilderMarkdownV2
//import ru.loginov.telegram.api.util.markdown2
//import javax.annotation.PostConstruct
//
//abstract class AbstractBotCommandWithSubCommands : AbstractBotCommand() {
//
//
//    protected abstract val subCommands: List<BotCommand>
//    private lateinit var subCommandsMap: Map<String, BotCommand>
//    private lateinit var subCommandsVariants: Map<String, String>
//
//    final override val usage: StringBuilderMarkdownV2?
//        get() = markdown2 {
//            append("/$commandName <subcommand_name>")
//            subCommands.forEach { subCommand ->
//                append('\n')
//                bold {
//                    append("Sub command '${subCommand.commandName}'")
//                }
//                append('\n')
//
//                subCommand.description?.also {
//                    append(it)
//                    append('\n')
//                }
//                subCommand.usage?.also {
//                    italic {
//                        append(it)
//                    }
//                    append('\n')
//                }
//            }
//        }
//
//    @PostConstruct
//    fun init() {
//        subCommandsMap = subCommands.associateBy { it.commandName }
//        subCommandsVariants = subCommands.associate { (it.simpleName ?: it.commandName) to it.commandName }
//    }
//
//    override suspend fun execute(context: BotCommandExecuteContext) {
//        val commandName = context.argumentManager.getNextArgument(subCommandsVariants, "subcommand")
//        subCommandsMap[commandName]?.execute(context) ?: context.sendMessage {
//            buildText {
//                append("Can not find subcommand with $commandName")
//            }
//        }
//    }
//}