package ru.loginov.serbian.bot.telegram.command.impl.category

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.withLocalization
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand(parents = [CategoryBotCommand::class])
//TODO: Add @RequiredPermission("commands.category.translate")
class SubCommandTranslateForCategory : AbstractSubCommand() {

    @Autowired
    private lateinit var categoryManager: CategoryManager

    @Autowired
    private lateinit var localizationManager: LocalizationManager

    override val commandName: String = "translate"
    override val shortDescription: String = "@{bot.command.category.translate._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.withLocalization("bot.command.category.translate._argument") {
            val categoryId = argument("categoryId", "categoryId")
                    .required()
                    .transform { it.toIntOrNull() }
                    .validate { it != null }
                    .get()!!

            if (!categoryManager.existsById(categoryId)) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.category.translate._error.can.not.find.category}{$categoryId}")
                    }
                }
                return
            }

            val lang = argument("lang", "lang").requiredAndGet()

            if (!localizationManager.isSupport(lang)) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.category.translate._error.language.is.not.support}{$lang}")
                    }
                }
                return
            }

            val translate = argument("translate", "translate").requiredAndGet()

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
}