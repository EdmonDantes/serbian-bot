package ru.loginov.serbian.bot.telegram.command.impl.category

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand(parents = [CategoryBotCommand::class])
@RequiredPermission("bot.command.category.translate")
class SubCommandTranslateForCategory : AbstractSubCommand() {

    @Autowired
    private lateinit var categoryManager: CategoryManager

    @Autowired
    private lateinit var localizationManager: LocalizationManager

    override val commandName: String = "translate"
    override val shortDescription: String = "@{bot.command.category.translate._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val categoryIdStr = context.getNextArgument("@{bot.command.category.translate._argument.categoryId}")
        val categoryId = categoryIdStr?.toIntOrNull()

        if (categoryId == null || !categoryManager.containsWithId(categoryId)) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.category.translate._error.can.not.find.category}{$categoryId}")
                }
            }
            return
        }

        val lang = context.getNextLanguageArgument("@{bot.command.category.translate._argument.lang}")

        if (lang.isNullOrEmpty() || !localizationManager.isSupport(lang)) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.category.translate._error.language.is.not.support}{$lang}")
                }
            }
            return
        }

        val translate = context.getNextArgument("@{bot.command.category.translate._argument.translate}")

        if (translate.isNullOrEmpty()) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.category.translate._error.translate.is.empty}")
                }
            }
            return
        }

        try {
            if (categoryManager.changeLocalization(categoryId, lang, translate)) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.cateogry.translate._success}{$translate}{$categoryId}")
                    }
                }
            } else {
                error("Can not change localization name for category")
            }
        } catch (e: Exception) {
            throw e
        }
    }
}