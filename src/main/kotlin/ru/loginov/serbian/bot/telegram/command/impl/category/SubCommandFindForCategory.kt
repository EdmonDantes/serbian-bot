package ru.loginov.serbian.bot.telegram.command.impl.category

import io.github.edmondantes.simple.localization.impl.localizationKey
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
@RequiredPermission("commands.category.find")
class SubCommandFindForCategory(
        private val categoryManager: CategoryManager
) : LocalizedSubCommand("bot.command.category.find") {
    override val commandName: String = "find"

    override suspend fun BotCommandExecuteContext.action() {
        val name = arguments.argument("name").requiredAndGet()

        val categories = categoryManager.findByName(name).mapNotNull {
            it.name?.let { name ->
                it.localizedId?.id?.let { id ->
                    name to id
                }
            }
        }

        sendMessage {
            markdown2(localization) {
                if (categories.isEmpty()) {
                    append(localizationKey("_error.can.not.find.category", name))
                } else {
                    append(localizationKey("_success"))
                    append('\n')
                    categories.forEach {
                        append("${it.first} -> ${it.second}\n")
                    }
                }
            }
        }
    }
}