package ru.loginov.serbian.bot.telegram.command.impl.settings

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.user.UserManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2
import ru.loginov.simple.permissions.annotation.RequiredPermission

@Component
@SubCommand(parents = [SettingsBotCommand::class])
@RequiredPermission("commands.settings.different.input.language")
class SubCommandDifferentInputLanguageForSettings : AbstractSubCommand() {

    @Autowired
    private lateinit var userManager: UserManager

    override val commandName: String = "differentInputLanguage"
    override val shortDescription: String = "@{bot.command.settings.differentInputLanguage._shortDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val shouldChange = context.choose(
                "shouldChange",
                "@{bot.command.settings.differentInputLanguage._argument.shouldChange}{${context.user.canInputDifferentLanguages ?: false}}"
        ).requiredAndGet()

        if (!shouldChange) {
            return
        }

        val newValue = context.choose("@{bot.command.settings.differentInputLanguage._argument.property}").requiredAndGet()

        userManager.update(context.user.id ?: error("Can not find user id"), canInputDifferentLanguages = newValue)
        context.user.canInputDifferentLanguages = newValue

        context.sendMessage {
            markdown2(context) {
                append("@{bot.command.settings.differentInputLanguage._success}{$newValue}")
            }
        }


    }
}