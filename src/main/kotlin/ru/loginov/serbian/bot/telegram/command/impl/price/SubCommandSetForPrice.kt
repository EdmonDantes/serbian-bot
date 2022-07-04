package ru.loginov.serbian.bot.telegram.command.impl.price

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.price.PriceDescriptionManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.LocalizedSubCommand
import ru.loginov.serbian.bot.util.markdown2
import ru.loginov.simple.localization.impl.localizationKey
import ru.loginov.simple.localization.impl.singleRequest

@Component
@SubCommand([PriceCommand::class])
class SubCommandSetForPrice(
        private val priceDescriptionManager: PriceDescriptionManager
) : LocalizedSubCommand("bot.command.price.set") {
    override val commandName: String = "set"

    override suspend fun BotCommandExecuteContext.action() {
        val shopId = arguments.argument("shopId")
                .required()
                .transform { it.toIntOrNull() }
                .validate { it != null }
                .get()!!

        val isUseCategory = arguments.choose("isUseCategory").requiredAndGet()

        val descriptionElementId = arguments.argument(
                "descriptionElementId",
                singleRequest(if (isUseCategory) "categoryId" else "productId")
        )
                .required()
                .transform { it.toIntOrNull() }
                .validate { it != null }
                .get()!!

        val minPrice = arguments.argument("minPrice").required()
                .transform { it.toFloatOrNull() }
                .validate { it != null }
                .get()!!

        val maxPrice = arguments.argument("maxPrice").required()
                .transform { it.toFloatOrNull() }
                .validate { it != null }
                .get()!!

        try {
            val dto = if (isUseCategory) {
                priceDescriptionManager.createOrUpdateForCategory(descriptionElementId, shopId, minPrice, maxPrice)
            } else {
                priceDescriptionManager.createOrUpdateForProduct(descriptionElementId, shopId, minPrice, maxPrice)
            }
            if (dto != null) {
                sendMessage {
                    markdown2(localization) {
                        append(localizationKey("_success"))
                    }
                }
                return
            }
        } catch (e: Exception) {
            LOGGER.warn("Can not update price", e)
        }

        sendMessage {
            markdown2(localization) {
                append(localizationKey("_error"))
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandSetForPrice::class.java)
    }
}