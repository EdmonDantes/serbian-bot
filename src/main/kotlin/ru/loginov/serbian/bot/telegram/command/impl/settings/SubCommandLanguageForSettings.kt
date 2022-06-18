package ru.loginov.serbian.bot.telegram.command.impl.settings

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.data.manager.user.UserManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand(parents = [SettingsBotCommand::class])
class SubCommandLanguageForSettings : AbstractSubCommand() {

    @Autowired
    private lateinit var userManager: UserManager

    @Autowired
    private lateinit var localizationManager: LocalizationManager

    override val commandName: String = "language"
    override val shortDescription: String = "@{bot.command.settings.language._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val lang = context.user.language ?: localizationManager.defaultLanguage
        val langName = localizationManager.findLocalizedStringByKey(lang, "language.$lang") ?: lang

        val shouldChange = context.choose(
                "shouldChange",
                "@{bot.command.settings.language._argument.shouldChange}{$langName}"
        ).requiredAndGet()
        if (!shouldChange) {
            return
        }

        val newLang = context.language("newLang", "@{bot.command.settings.language._argument.lang}")
                .requiredAndGet()

        userManager.update(context.user.id ?: error("Can not find user id"), language = newLang)
        context.user.language = newLang

        val newLangName = localizationManager.findLocalizedStringByKey(newLang, "language.$newLang") ?: newLang

        context.sendMessage {
            markdown2(context) {
                append("@{bot.command.settings.language._success}{$newLangName}")
            }
        }
    }
}