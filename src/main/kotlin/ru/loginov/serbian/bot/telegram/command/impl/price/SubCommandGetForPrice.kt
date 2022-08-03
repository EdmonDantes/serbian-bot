package ru.loginov.serbian.bot.telegram.command.impl.price

import io.github.edmondantes.simple.localization.impl.localizationKey
import io.github.edmondantes.simple.localization.impl.singleRequest
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.data.manager.price.PriceDescriptionManager
import ru.loginov.serbian.bot.data.manager.product.ProductDescriptionManager
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.argument.requiredAndGet
import ru.loginov.serbian.bot.telegram.command.base.LocalizedSubCommand
import ru.loginov.serbian.bot.telegram.command.context.BotCommandExecuteContext
import ru.loginov.serbian.bot.telegram.util.markdown2

@Component
@SubCommand([PriceCommand::class])
class SubCommandGetForPrice(
        private val priceDescriptionManager: PriceDescriptionManager,
        private val categoryManager: CategoryManager,
        private val productDescriptionManager: ProductDescriptionManager
) : LocalizedSubCommand("bot.command.price.get") {
    override val commandName: String = "get"

    override suspend fun BotCommandExecuteContext.action() {
        val isUseCategory = arguments.choose("isUseCategory").requiredAndGet()
        val elementId = arguments.argument("elementId", singleRequest(if (isUseCategory) "categoryId" else "productId"))
                .required()
                .transform { it.toIntOrNull() }
                .validate { it != null }
                .get()!!


        val prices = if (isUseCategory) {
            priceDescriptionManager.findAllForCategory(elementId)
        } else {
            priceDescriptionManager.findAllForProduct(elementId)
        }.filter { it.shop != null && (isUseCategory && it.category != null || !isUseCategory && it.product != null) }

        if (prices.isEmpty()) {
            sendMessage {
                markdown2(localization) {
                    append(localizationKey("_.can.not.find.prices"))
                }
            }
            return
        }

        sendMessageWithoutLimit {
            markdown2(localization) {
                val dto = prices.first()
                if (isUseCategory) {
                    val categoryName = categoryManager.findLocalizedNameFor(
                            dto.category!!,
                            user.language
                    )
                    append(localizationKey("_.price.for.category", categoryName ?: ""))
                } else {
                    val productName = productDescriptionManager.findLocalizedNameFor(
                            dto.product!!,
                            user.language
                    )
                    append(localizationKey("_.price.for.product", productName ?: ""))
                }
                append("\n")
                prices.associateBy { it.shopId }.forEach { (_, price) ->
                    val shop = price.shop!!
                    append('\n')
                    append('\n')
                    append(
                            localizationKey(
                                    "_.shop.description",
                                    shop.shopName ?: "",
                                    shop.address ?: "",
                                    shop.googleMapLink ?: ""
                            )
                    )
                    append('\n')
                    append(
                            localizationKey(
                                    "_.price.description",
                                    price.minPrice?.toString() ?: "",
                                    price.maxPrice?.toString() ?: ""
                            )
                    )
                }
            }
        }
    }
}