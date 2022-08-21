package ru.loginov.serbian.bot.telegram.command.impl.category

import io.github.edmondantes.simple.localization.impl.localizationKey
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.base.LocalizedSubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.util.markdown2
import ru.loginov.simple.permissions.annotation.RequiredPermission

@Component
@SubCommand(parents = [CategoryBotCommand::class])
@RequiredPermission("commands.category.create")
class SubCommandCreateForCategory(
        private val categoryManager: CategoryManager
) : LocalizedSubCommand("bot.command.category.create") {

    override val commandName: String = "create"

    override suspend fun BotCommandExecuteContext.action() {
        val categoryName = arguments.argument("categoryName").requiredAndGet()
        if (categoryName.isEmpty()) {
            sendMessage {
                markdown2(localization) {
                    append(localizationKey("_.can.not.create.category", categoryName))
                }
            }
            return
        }

        val parentCategoryId = arguments.argument("parentCategoryId")
                .optional()
                .transform { it.toIntOrNull() }
                .validate { it != null }
                .getOrNull()

        val lang = "Ks" // FIXME: !!!!!
//        val lang = user.getInputLanguageOr(suspend {
//            arguments.argument("lang", singleRequest("language")).requiredAndGet()
//        })

        sendMessage {
            markdown2(localization) {
                if (lang.isNullOrEmpty() || lang.length != 2) {
                    append(localizationKey("_.can.not.create.category.unknown.language"))
                } else {
                    val category = try {
                        categoryManager.create(mapOf(lang to categoryName), parentCategoryId)
                    } catch (e: Exception) {
                        LOGGER.warn("Can not create new category with name '$categoryName'", e)
                        null
                    }

                    if (category != null) {
                        append(localizationKey("_.success", categoryName, "${category.id!!}"))
                    } else {
                        append(localizationKey("_.can.not.create.category", categoryName))
                    }
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandCreateForCategory::class.java)
    }
}