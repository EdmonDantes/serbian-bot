package ru.loginov.serbian.bot.telegram.command.impl.price

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.price.PriceDescriptionManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.withLocalization
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand([PriceCommand::class])
class SubCommandSetForPrice(
        private val priceDescriptionManager: PriceDescriptionManager
) : AbstractSubCommand() {
    override val commandName: String = "set"
    override val actionDescription: String = "@{bot.command.price.set._shopDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.withLocalization("bot.command.price.set._argument") {
            val shopId = argument("shopId", "shopId")
                    .required()
                    .transform { it.toIntOrNull() }
                    .validate { it != null }
                    .get()!!

            val isUseCategory = choose("isUseCategory").requiredAndGet()

            val descriptionElementId = argument(
                    "descriptionElementId",
                    if (isUseCategory) "categoryId" else "productId"
            )
                    .required()
                    .transform { it.toIntOrNull() }
                    .validate { it != null }
                    .get()!!

            val minPrice = argument("minPrice", "minPrice").required()
                    .transform { it.toFloatOrNull() }
                    .validate { it != null }
                    .get()!!

            val maxPrice = argument("maxPrice", "maxPrice").required()
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
                    context.sendMessage {
                        markdown2(context) {
                            append("@{bot.command.price.set._success}")
                        }
                    }
                    return
                }
            } catch (e: Exception) {
                LOGGER.warn("Can not update price", e)
            }

            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.price.set._error}")
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandSetForPrice::class.java)
    }
}