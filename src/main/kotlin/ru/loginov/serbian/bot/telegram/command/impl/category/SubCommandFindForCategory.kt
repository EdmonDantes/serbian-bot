package ru.loginov.serbian.bot.telegram.command.impl.category

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.withLocalization
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand(parents = [CategoryBotCommand::class])
class SubCommandFindForCategory : AbstractSubCommand() {

    @Autowired
    private lateinit var categoryManager: CategoryManager

    override val commandName: String = "find"
    override val actionDescription: String = "@{bot.command.category.find._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.withLocalization("bot.command.category.find._argument") {
            val name = argument("name", "name").requiredAndGet()

            context.sendMessage {
                markdown2(context) {
                    val categories = categoryManager.findByName(name).mapNotNull {
                        it.name?.let { name ->
                            it.localizedId?.id?.let { id ->
                                name to id
                            }
                        }
                    }

                    if (categories.isEmpty()) {
                        append("@{bot.command.category.find._error.can.not.find.category}{$name}")
                    } else {
                        append("@{bot.command.category.find._success}\n")
                        categories.forEach {
                            append("${it.first} -> ${it.second}\n")
                        }
                    }
                }
            }
        }
    }
}