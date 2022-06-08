package ru.loginov.serbian.bot.telegram.command.impl.price

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.price.PriceDescriptionManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand([PriceCommand::class])
class SubCommandSetForPrice(
        private val priceDescriptionManager: PriceDescriptionManager
) : AbstractSubCommand() {
    override val commandName: String = "set"
    override val shortDescription: String = "@{bot.command.price.set._shopDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val shopIdStr = context.getNextArgument("@{bot.command.price.set._argument.shopId}")
                ?: error("shopId can not be null")
        val shopId = shopIdStr.toIntOrNull()
        if (shopId == null) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.price.set._invalid_argument.shopId}")
                }
            }
            return
        }

        val isUseCategory = context.getNextChooseArgument("@{bot.command.price.set._argument.isUseCategory}")
        val descriptionElementId = if (isUseCategory) {
            val categoryIdStr = context.getNextArgument("@{bot.command.price.set._argument.categoryId}")
                    ?: error("categoryId can not be null")
            val categoryId = categoryIdStr.toIntOrNull()
            if (categoryId == null) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.price.set._invalid_argument.categoryId}")
                    }
                }
                return
            }
            categoryId
        } else {
            val productIdStr = context.getNextArgument("@{bot.command.price.set._argument.productId}")
                    ?: error("productId can not be null")
            val productId = productIdStr.toIntOrNull()
            if (productId == null) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.price.set._invalid_argument.productId}")
                    }
                }
                return
            }
            productId
        }


        val minPriceStr = context.getNextArgument("@{bot.command.price.set._argument.minPrice}")
                ?: error("minPrice can not be null")
        val minPrice = minPriceStr.toFloatOrNull()
        if (minPrice == null) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.price.set._invalid_argument.minPrice}")
                }
            }
            return
        }
        val maxPriceStr = context.getNextArgument("@{bot.command.price.set._argument.maxPrice}")
                ?: error("maxPrice can not be null")
        val maxPrice = maxPriceStr.toFloatOrNull()
        if (maxPrice == null) {
            context.sendMessage {
                markdown2(context) {
                    append("@{bot.command.price.set._invalid_argument.maxPrice}")
                }
            }
            return
        }

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

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandSetForPrice::class.java)
    }
}