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
//TODO: Add in next version. After update permission manager. @RequiredPermission("commands.shop.find.by.name")
class SubCommandByNameForFind(
        private val shopManager: ShopDescriptionManager
) : AbstractSubCommand() {
    override val commandName: String = "byName"
    override val shortDescription: String = "@{bot.command.shop.find.byname._shopDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val name = context.argument("name", "@{bot.command.shop.find.byname._argument.name}").requiredAndGet()

        try {
            val shops = shopManager.findByName(name)
            context.sendMessage {
                markdown2(context) {
                    if (shops.isEmpty()) {
                        append("@{bot.command.shop.find.byname._.not.found.any.shops}{$name}")
                    } else {
                        append("@{bot.command.shop.find.byname._success}")
                        shops.forEach {
                            append("\n")
                            append("@{bot.command.shop.find.byname._success.shop.description}{${it.id}}{${it.shopName}}{${it.address}}{${it.googleMapLink ?: "---"}}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            LOGGER.warn("Can not find shops by name '$name'", e)
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.shop.find.byname._error}")
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandByNameForFind::class.java)
    }
}