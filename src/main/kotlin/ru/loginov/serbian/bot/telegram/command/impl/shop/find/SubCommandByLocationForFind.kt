package ru.loginov.serbian.bot.telegram.command.impl.shop.find

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.shop.ShopDescriptionManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand([SubCommandFindForShop::class])
//TODO: Add in next version. After update permission manager. @RequiredPermission("commands.shop.find.by.location")
class SubCommandByLocationForFind(
        private val shopManager: ShopDescriptionManager
) : AbstractSubCommand() {
    override val commandName: String = "byLocation"
    override val actionDescription: String = "@{bot.command.shop.find.bylocation._shopDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val location = context.location("location", "@{bot.command.shop.find.bylocation._argument.location}")
                .requiredAndGet()

        try {
            val shops = shopManager.findNearest(location.first, location.second)

            context.sendMessage {
                markdown2(context) {
                    if (shops.isEmpty()) {
                        append("@{bot.command.shop.find.bylocation._.not.found.any.shops}")
                    } else {
                        append("@{bot.command.shop.find.bylocation._success}")
                        shops.forEach {
                            append("\n")
                            append("@{bot.command.shop.find.bylocation._success.shop.description}{${it.id}}{${it.shopName}}{${it.address}}{${it.googleMapLink ?: "---"}}")
                        }
                    }
                }
            }
            return
        } catch (e: Exception) {
            LOGGER.warn("Can not find shops near location '$location'", e)
        }

        context.sendMessage {
            markdown2(context) {
                append("@{bot.command.shop.find.bylocation._error}")
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandByLocationForFind::class.java)
    }
}