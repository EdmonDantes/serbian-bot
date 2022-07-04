package ru.loginov.serbian.bot.telegram.command.impl.category

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.LocalizedSubCommand
import ru.loginov.serbian.bot.util.markdown2
import ru.loginov.simple.localization.impl.localizationKey
import ru.loginov.simple.localization.manager.LocalizationManager

@Component
@SubCommand(parents = [CategoryBotCommand::class])
//TODO: Add @RequiredPermission("commands.category.translate")
class SubCommandTranslateForCategory(
        private val categoryManager: CategoryManager,
        private val localizationManager: LocalizationManager
) : LocalizedSubCommand("bot.command.category.translate") {

    override val commandName: String = "translate"

    override suspend fun BotCommandExecuteContext.action() {
        val categoryId = arguments.argument("categoryId")
                .required()
                .transform { it.toIntOrNull() }
                .validate { it != null }
                .get()!!

        if (!categoryManager.existsById(categoryId)) {
            sendMessage {
                markdown2(localization) {
                    append(localizationKey("_error.can.not.find.category", categoryId))
                }
            }
            return
        }

        val lang = arguments.argument("lang").requiredAndGet()

        if (!localizationManager.isSupport(lang)) {
            sendMessage {
                markdown2(localization) {
                    append(localizationKey("_error.language.is.not.support", lang))
                }
            }
            return
        }

        val translate = arguments.argument("translate").requiredAndGet()

        if (categoryManager.changeLocalization(categoryId, lang, translate)) {
            sendMessage {
                markdown2(localization) {
                    append(localizationKey("_success", translate, categoryId))
                }
            }
        } else {
            error("Can not change localization name for category")
        }
    }
}