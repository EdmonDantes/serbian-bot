package ru.loginov.serbian.bot.telegram.command.impl.price.set

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.price.PriceDescriptionManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.telegram.command.impl.price.PriceCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand([PriceCommand::class])
class SubCommandSetForPrice(
        private val priceDescriptionManager: PriceDescriptionManager
) : AbstractSubCommand() {
    override val commandName: String = "set"
    override val shortDescription: String = "@{}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        val shopIdStr = context.getNextArgument("@{}") ?: error("shopId can not be null")
        val shopId = shopIdStr.toIntOrNull()
        if (shopId == null) {
            context.sendMessage {
                markdown2(context) {
                    append("@{}")
                }
            }
            return
        }

        val isUseCategory = context.getNextChooseArgument("@{}")
        val descriptionElementId = if (isUseCategory) {
            val categoryIdStr = context.getNextArgument("@{}") ?: error("categoryId can not be null")
            val categoryId = categoryIdStr.toIntOrNull()
            if (categoryId == null) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{}")
                    }
                }
                return
            }
            categoryId
        } else {
            val productIdStr = context.getNextArgument("@{}") ?: error("productId can not be null")
            val productId = productIdStr.toIntOrNull()
            if (productId == null) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{}")
                    }
                }
                return
            }
            productId
        }


        val minPriceStr = context.getNextArgument("@{}") ?: error("minPrice can not be null")
        val minPrice = minPriceStr.toFloatOrNull()
        if (minPrice == null) {
            context.sendMessage {
                markdown2(context) {
                    append("@{}")
                }
            }
            return
        }
        val maxPriceStr = context.getNextArgument("@{}") ?: error("maxPrice can not be null")
        val maxPrice = maxPriceStr.toFloatOrNull()
        if (maxPrice == null) {
            context.sendMessage {
                markdown2(context) {
                    append("@{}")
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
                        append("@{}")
                    }
                }
                return
            }
        } catch (e: Exception) {
            LOGGER.warn("Can not update price", e)
        }

        context.sendMessage {
            markdown2(context) {
                append("@{}")
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandSetForPrice::class.java)
    }
}