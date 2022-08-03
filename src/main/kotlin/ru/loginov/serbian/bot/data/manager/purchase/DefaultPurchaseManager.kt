package ru.loginov.serbian.bot.data.manager.purchase

import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.dto.purchase.PurchaseDescription
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.data.manager.shop.ShopDescriptionManager
import ru.loginov.serbian.bot.data.repository.purchase.PurchaseDescriptionRepository
import ru.loginov.serbian.bot.util.saveOr
import ru.loginov.serbian.bot.util.useSuspend

@Service
class DefaultPurchaseManager(
        private val purchaseDescriptionRepository: PurchaseDescriptionRepository,
        private val categoryManager: CategoryManager,
        private val shopManager: ShopDescriptionManager
) : PurchaseManager {
    override suspend fun createForCategory(categoryId: Int, shopId: Int, price: Float): PurchaseDescription? {
        if (!categoryManager.existsById(categoryId) || !shopManager.existsById(shopId)) {
            return null
        }

        val result = PurchaseDescription()
        result.categoryId = categoryId
        result.shopId = shopId
        result.price = price

        return purchaseDescriptionRepository.useSuspend {
            it.saveOr(result) { null }
        }
    }

    override suspend fun createForProduct(productId: Int, shopId: Int, price: Float): PurchaseDescription? =
            null


}