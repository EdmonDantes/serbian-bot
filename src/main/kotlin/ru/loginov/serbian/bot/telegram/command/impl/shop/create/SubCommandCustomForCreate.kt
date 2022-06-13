package ru.loginov.serbian.bot.telegram.command.impl.shop.create

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.shop.ShopDescriptionManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2
import ru.loginov.simple.permissions.annotation.RequiredPermission

@Component
@SubCommand([SubCommandCreateForShop::class])
@RequiredPermission("commands.shop.create.custom")
class SubCommandCustomForCreate(
        private val shopDescriptionManager: ShopDescriptionManager
) : AbstractSubCommand() {
    override val commandName: String = "custom"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val name = context.getNextArgument("@{bot.command.shop.create.custom._argument.name}")
                ?: error("Name can not be null")
        val address = context.getNextArgument("@{bot.command.shop.create.custom._argument.address}")
                ?: error("Address can not be null")
        val floorStr = context.getNextArgument("@{bot.command.shop.create.custom._argument.floor}", true)
        val floor = floorStr?.toIntOrNull()

        if (floorStr != null && floor == null) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.shop.create.custom._.floor.should.be.number}")
                }
            }
            return
        }


        try {
            val shop = shopDescriptionManager.create(name, address, floor)
            if (shop != null) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.shop.create.custom._success}{${shop.shopName}{${shop.id}")
                    }
                }
                return
            }
        } catch (e: Exception) {
            LOGGER.warn("Can not save shop with name '$name', address '$address' and floor '$floor'", e)
        }

        context.sendMessage {
            markdown2(context) {
                append("@{bot.command.shop.create.custom._failed}")
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandCustomForCreate::class.java)
    }
}