package ru.loginov.serbian.bot.telegram.command.impl.shop

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.shop.ShopDescriptionManager
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand([ShopBotCommand::class])
@RequiredPermission("commands.shop.create")
class SubCommandCreateForShop(
        private val shopDescriptionManager: ShopDescriptionManager
) : AbstractSubCommand() {

    override val commandName: String = "create"
    override val shortDescription: String = "@{bot.command.shop.create._shopDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val useGoogle = context.getNextChooseArgument("@{bot.command.shop.create._argument.useGoogle}")

        if (useGoogle) {
            createByGoogleLink(context)
        } else {
            createByNameAndAddress(context)
        }
    }

    private suspend fun createByGoogleLink(context: BotCommandExecuteContext) {
        val googleShareLink = context.getNextArgument("@{bot.command.shop.create._argument.googleShareLink}")
                ?: error("Google share link can not be null")
        val floorStr = context.getNextArgument("@{bot.command.shop.create._argument.floor}", true)
        val floor = floorStr?.toIntOrNull()

        if (floorStr != null && floor == null) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.shop.create._.floor.should.be.number}")
                }
            }
            return
        }

        try {
            val shop = shopDescriptionManager.create(googleShareLink, floor)
            if (shop != null) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.shop.create._success}{${shop.shopName}{${shop.id}")
                    }
                }
                return
            }
        } catch (e: Exception) {
            LOGGER.warn("Can not save shop from google map share link '$googleShareLink' and floor '$floor'", e)
        }

        context.sendMessage {
            markdown2(context) {
                append("@{bot.command.shop.create._failed}")
            }
        }
    }

    private suspend fun createByNameAndAddress(context: BotCommandExecuteContext) {
        val name = context.getNextArgument("@{bot.command.shop.create._argument.name}")
                ?: error("Name can not be null")
        val address = context.getNextArgument("@{bot.command.shop.create._argument.address}")
                ?: error("Address can not be null")
        val floorStr = context.getNextArgument("@{bot.command.shop.create._argument.floor}", true)
        val floor = floorStr?.toIntOrNull()

        if (floorStr != null && floor == null) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.shop.create._.floor.should.be.number}")
                }
            }
            return
        }


        try {
            val shop = shopDescriptionManager.create(name, address, floor)
            if (shop != null) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.shop.create._success}{${shop.shopName}{${shop.id}")
                    }
                }
                return
            }
        } catch (e: Exception) {
            LOGGER.warn("Can not save shop with name '$name', address '$address' and floor '$floor'", e)
        }

        context.sendMessage {
            markdown2(context) {
                append("@{bot.command.shop.create._failed}")
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandCreateForShop::class.java)
    }
}