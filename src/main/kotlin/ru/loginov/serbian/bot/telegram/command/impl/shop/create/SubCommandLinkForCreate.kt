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

@Component
@SubCommand([SubCommandCreateForShop::class])
//TODO: Add in next version. After update permission manager. @RequiredPermission("commands.shop.create.link")
class SubCommandLinkForCreate(
        private val shopDescriptionManager: ShopDescriptionManager
) : LocalizedSubCommand("bot.command.shop.create.link") {
    override val commandName: String = "link"

    override suspend fun BotCommandExecuteContext.action() {
        val googleShareLink = arguments.argument("googleShareLink").requiredAndGet()
        val floor = arguments.argument("floor").optional()
                .transform { it.toIntOrNull() }
                .validate { it != null }
                .getOrNull()

        try {
            val shop = shopDescriptionManager.create(googleShareLink, floor)
            if (shop != null) {
                sendMessage {
                    markdown2(localization) {
                        append(localizationKey("_success", shop.shopName ?: "", shop.id?.toString() ?: ""))
                    }
                }
                return
            }
        } catch (e: Exception) {
            LOGGER.warn("Can not save shop from google map share link '$googleShareLink' and floor '$floor'", e)
        }

        sendMessage {
            markdown2(localization) {
                append(localizationKey("_failed"))
            }
        }
    }


    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandLinkForCreate::class.java)
    }

}