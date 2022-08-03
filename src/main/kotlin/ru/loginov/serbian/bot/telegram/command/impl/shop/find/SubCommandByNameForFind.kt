package ru.loginov.serbian.bot.telegram.command.impl.shop.find

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
@SubCommand([SubCommandFindForShop::class])
//TODO: Add in next version. After update permission manager. @RequiredPermission("commands.shop.find.by.name")
class SubCommandByNameForFind(
        private val shopManager: ShopDescriptionManager
) : LocalizedSubCommand("bot.command.shop.find.byname") {
    override val commandName: String = "byName"

    override suspend fun BotCommandExecuteContext.action() {
        val name = arguments.argument("name").requiredAndGet()

        try {
            val shops = shopManager.findByName(name)
            sendMessage {
                markdown2(localization) {
                    if (shops.isEmpty()) {
                        append(localizationKey("_.not.found.any.shops", name))
                    } else {
                        append(localizationKey("_success"))
                        shops.forEach {
                            append("\n")
                            append(
                                    localizationKey(
                                            "_success.shop.description",
                                            it.id?.toString() ?: "",
                                            it.shopName ?: "",
                                            it.address ?: "",
                                            it.googleMapLink ?: "---"
                                    )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            LOGGER.warn("Can not find shops by name '$name'", e)
            sendMessage {
                markdown2(localization) {
                    append(localizationKey("_error"))
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandByNameForFind::class.java)
    }
}