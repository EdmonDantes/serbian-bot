package ru.loginov.serbian.bot.telegram.command.impl.settings

import io.github.edmondantes.simple.localization.Localizer
import io.github.edmondantes.simple.localization.impl.localizationKey
import io.github.edmondantes.simple.localization.impl.singleRequest
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.user.UserManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.base.LocalizedSubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.util.markdown2

@Component
@SubCommand(parents = [SettingsBotCommand::class])
class SubCommandLanguageForSettings(
        private val userManager: UserManager,
        private val rootLocalizer: Localizer
) : LocalizedSubCommand("bot.command.settings.differentInputLanguage") {

    override val commandName: String = "language"
    override fun getActionDescription(context: BotCommandExecuteContext): String? =
            context.localization.localize(localizationKey("bot.command.settings.language._actionDescription"))

    override suspend fun BotCommandExecuteContext.action() {
        val lang = user.language ?: rootLocalizer.defaultLanguage
        val langName = rootLocalizer.localizeOrDefault(lang, localizationKey("language.$lang"), lang)

        val shouldChange = arguments.choose("shouldChange", singleRequest("shouldChange", langName)).requiredAndGet()
        if (!shouldChange) {
            return
        }

        val newLang = arguments.language("newLang").requiredAndGet()

//        userManager.update(user.id ?: error("Can not find user id"), language = newLang)
        user.language = newLang

        val newLangName = rootLocalizer.localizeOrDefault(newLang, localizationKey("language.$newLang"), newLang)

        sendMessage {
            markdown2(localization) {
                append("@{bot.command.settings.language._success}{$newLangName}")
            }
        }
    }
}