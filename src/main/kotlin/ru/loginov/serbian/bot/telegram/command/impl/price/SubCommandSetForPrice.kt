package ru.loginov.serbian.bot.telegram.command.impl.price

import io.github.edmondantes.simple.localization.impl.localizationKey
import io.github.edmondantes.simple.localization.impl.singleRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.purchase.PurchaseManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.base.LocalizedSubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.util.markdown2

@Component
@SubCommand([PriceCommand::class])
class SubCommandSetForPrice(
        private val purchaseManager: PurchaseManager
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

        val price = arguments.argument("price").required()
                .transform { it.toFloatOrNull() }
                .validate { it != null }
                .get()!!

        try {
            val dto = if (isUseCategory) {
                purchaseManager.createForCategory(descriptionElementId, shopId, price)
            } else {
                purchaseManager.createForProduct(descriptionElementId, shopId, price)
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