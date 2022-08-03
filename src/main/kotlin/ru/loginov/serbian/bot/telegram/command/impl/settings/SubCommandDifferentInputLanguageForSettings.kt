package ru.loginov.serbian.bot.telegram.command.impl.settings

import io.github.edmondantes.simple.localization.impl.singleRequest
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.user.UserManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.base.LocalizedSubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.util.markdown2
import ru.loginov.simple.permissions.annotation.RequiredPermission

@Component
@SubCommand(parents = [SettingsBotCommand::class])
@RequiredPermission("commands.settings.different.input.language")
class SubCommandDifferentInputLanguageForSettings(private val userManager: UserManager) :
        LocalizedSubCommand("bot.command.settings.differentInputLanguage") {

    override val commandName: String = "differentInputLanguage"

    override suspend fun BotCommandExecuteContext.action() {
        val shouldChange = arguments.choose(
                "shouldChange",
                singleRequest("shouldChange", "${user.canInputDifferentLanguages ?: false}")
        ).requiredAndGet()

        if (!shouldChange) {
            return
        }

        val newValue = arguments.choose("property").requiredAndGet()

        userManager.update(user.id ?: error("Can not find user id"), canInputDifferentLanguages = newValue)
        user.canInputDifferentLanguages = newValue

        sendMessage {
            markdown2(localization) {
                append("@{bot.command.settings.differentInputLanguage._success}{$newValue}")
            }
        }
    }

}