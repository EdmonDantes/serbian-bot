package ru.loginov.serbian.bot.telegram.command.impl.settings

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.user.UserManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.LocalizedSubCommand
import ru.loginov.serbian.bot.util.markdown2
import ru.loginov.simple.localization.impl.localizationKey
import ru.loginov.simple.localization.impl.singleRequest
import ru.loginov.simple.localization.manager.LocalizationManager

@Component
@SubCommand(parents = [SettingsBotCommand::class])
class SubCommandLanguageForSettings : LocalizedSubCommand("bot.command.settings.differentInputLanguage") {

    @Autowired
    private lateinit var userManager: UserManager

    @Autowired
    private lateinit var localizationManager: LocalizationManager

    override val commandName: String = "language"
    override fun getActionDescription(context: BotCommandExecuteContext): String? =
            context.localization.localize(localizationKey("bot.command.settings.language._actionDescription"))

    override suspend fun BotCommandExecuteContext.action() {
        val lang = user.language ?: localizationManager.defaultLanguage
        val langName = localization.localizeOrDefault(localizationKey("language.$lang"), lang)

        val shouldChange = arguments.choose("shouldChange", singleRequest("shouldChange", langName)).requiredAndGet()
        if (!shouldChange) {
            return
        }

        val newLang = arguments.language("newLang").requiredAndGet()

        userManager.update(user.id ?: error("Can not find user id"), language = newLang)
        user.language = newLang

        val newLangName = localizationManager.localizeOrDefault(newLang, localizationKey("language.$newLang"), newLang)

        sendMessage {
            markdown2(localization) {
                append("@{bot.command.settings.language._success}{$newLangName}")
            }
        }
    }
}