package ru.loginov.serbian.bot.telegram.command.impl.category

import org.slf4j.LoggerFactory
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
//TODO: Add @RequiredPermission("commands.category.create")
class SubCommandCreateForCategory : AbstractSubCommand() {

    @Autowired
    private lateinit var categoryManager: CategoryManager

    override val commandName: String = "create"
    override val shortDescription: String? = "@{bot.command.category.create._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.withLocalization("bot.command.category.create._argument") {
            val categoryName = context.argument("categoryName", "categoryName").requiredAndGet()

            if (categoryName.isEmpty()) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.category.create._.can.not.create.category}{$categoryName}")
                    }
                }
                return
            }


            val parentCategoryId = context.argument("parentCategoryId", "parentCategoryId")
                    .required()
                    .transform { it.toIntOrNull() }
                    .validateValue { it != null }
                    .get()!!

            val lang = context.user.getInputLanguageOr(suspend {
                context.argument("lang", "language").requiredAndGet()
            })

            context.sendMessage {
                markdown2(context) {
                    if (lang.isNullOrEmpty() || lang.length != 2) {
                        append("@{bot.command.category.create._.can.not.create.category.unknown.language}")
                    } else {
                        val category = try {
                            categoryManager.create(mapOf(lang to categoryName), parentCategoryId)
                        } catch (e: Exception) {
                            LOGGER.warn("Can not create new category with name '$categoryName'", e)
                            null
                        }

                        if (category != null) {
                            append("@{bot.command.category.create._.success}{$categoryName}{${category.id}}")
                        } else {
                            append("@{bot.command.category.create._.can.not.create.category}{$categoryName}")
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandCreateForCategory::class.java)
    }
}