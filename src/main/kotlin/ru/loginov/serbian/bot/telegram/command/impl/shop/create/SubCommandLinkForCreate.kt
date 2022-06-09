package ru.loginov.serbian.bot.telegram.command.impl.shop.create

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.shop.ShopDescriptionManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand([SubCommandCreateForShop::class])
//TODO: Add in next version. After update permission manager. @RequiredPermission("commands.shop.create.link")
class SubCommandLinkForCreate(
        private val shopDescriptionManager: ShopDescriptionManager
) : AbstractSubCommand() {
    override val commandName: String = "link"
    override val shortDescription: String = "@{bot.command.shop.create.link._shopDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val googleShareLink = context.getNextArgument("@{bot.command.shop.create.link._argument.googleShareLink}")
                ?: error("Google share link can not be null")
        val floorStr = context.getNextArgument("@{bot.command.shop.create.link._argument.floor}", true)
        val floor = floorStr?.toIntOrNull()

        if (floorStr != null && floor == null) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.shop.create.link._.floor.should.be.number}")
                }
            }
            return
        }

        try {
            val shop = shopDescriptionManager.create(googleShareLink, floor)
            if (shop != null) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.shop.create.link._success}{${shop.shopName}}{${shop.id}}")
                    }
                }
                return
            }
        } catch (e: Exception) {
            LOGGER.warn("Can not save shop from google map share link '$googleShareLink' and floor '$floor'", e)
        }

        context.sendMessage {
            markdown2(context) {
                append("@{bot.command.shop.create.link._failed}")
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandLinkForCreate::class.java)
    }

}