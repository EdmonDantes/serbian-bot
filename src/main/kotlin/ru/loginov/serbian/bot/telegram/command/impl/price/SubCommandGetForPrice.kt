package ru.loginov.serbian.bot.telegram.command.impl.price

import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.data.manager.price.PriceDescriptionManager
import ru.loginov.serbian.bot.data.manager.product.ProductDescriptionManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.manager.impl.withLocalization
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.command.impl.AbstractSubCommand
import ru.loginov.serbian.bot.util.markdown2

@Component
@SubCommand([PriceCommand::class])
class SubCommandGetForPrice(
        private val priceDescriptionManager: PriceDescriptionManager,
        private val categoryManager: CategoryManager,
        private val productDescriptionManager: ProductDescriptionManager
) : AbstractSubCommand() {
    override val commandName: String = "get"
    override val shortDescription: String? = "@{bot.command.price.get._shopDescription}"

    override suspend fun execute(context: BotCommandExecuteContext) {
        context.withLocalization("bot.command.price.get._argument") {
            val isUseCategory = choose("isUseCategory", "isUseCategory").requiredAndGet()
            val elementId = argument("elementId", if (isUseCategory) "categoryId" else "productId")
                    .required()
                    .transform { it.toIntOrNull() }
                    .validate { it != null }
                    .get()!!


            val prices = if (isUseCategory) {
                priceDescriptionManager.findByCategory(elementId)
            } else {
                priceDescriptionManager.findByProduct(elementId)
            }.filter { it.shop != null && (isUseCategory && it.category != null || !isUseCategory && it.product != null) }

            if (prices.isEmpty()) {
                context.sendMessage {
                    markdown2(context) {
                        append("@{bot.command.price.get._.can.not.find.prices}")
                    }
                }
                return
            }

            context.sendMessageWithoutLimit {
                markdown2(context) {
                    val dto = prices.first()
                    if (isUseCategory) {
                        val categoryName = categoryManager.findLocalizedNameFor(
                                dto.category!!,
                                context.user.language
                        )
                        append("@{bot.command.price.get._.price.for.category}{$categoryName}")
                    } else {
                        val productName = productDescriptionManager.findLocalizedNameFor(
                                dto.product!!,
                                context.user.language
                        )
                        append("@{bot.command.price.get._.price.for.product}{$productName}")
                    }
                    append("\n")
                    prices.associateBy { it.shopId }.forEach { (_, price) ->
                        val shop = price.shop!!
                        append('\n')
                        append('\n')
                        append("@{bot.command.price.get._.shop.description}{${shop.shopName}}{${shop.address}}{${shop.googleMapLink}}")
                        append('\n')
                        append("@{bot.command.price.get._.price.description}{${price.minPrice}}{${price.maxPrice}}")
                    }
                }
            }

        }
    }
}