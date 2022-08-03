package ru.loginov.serbian.bot.telegram.command.impl.shop.create

import io.github.edmondantes.simple.localization.impl.localizationKey
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.shop.ShopDescriptionManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.base.LocalizedSubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.util.markdown2
import ru.loginov.simple.permissions.annotation.RequiredPermission

@Component
@SubCommand([SubCommandCreateForShop::class])
@RequiredPermission("commands.shop.create.custom")
class SubCommandCustomForCreate(
        private val shopDescriptionManager: ShopDescriptionManager
) : LocalizedSubCommand("bot.command.shop.create.custom") {
    override val commandName: String = "custom"

    override suspend fun BotCommandExecuteContext.action() {
        val name = arguments.argument("name").requiredAndGet()
        val address = arguments.argument("address").requiredAndGet()
        val floor = arguments.argument("floor")
                .optional()
                .transform { it.toIntOrNull() }
                .validate { it != null }
                .getOrNull()

        try {
            val shop = shopDescriptionManager.create(name, address, floor)
            if (shop != null) {
                sendMessage {
                    markdown2(localization) {
                        append(localizationKey("_success", shop.shopName ?: "", shop.id?.toString() ?: ""))
                    }
                }
                return
            }
        } catch (e: Exception) {
            LOGGER.warn("Can not save shop with name '$name', address '$address' and floor '$floor'", e)
        }

        sendMessage {
            markdown2(localization) {
                append(localizationKey("_failed"))
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandCustomForCreate::class.java)
    }
}