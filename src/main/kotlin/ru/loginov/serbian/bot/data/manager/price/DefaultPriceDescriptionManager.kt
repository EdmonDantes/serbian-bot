package ru.loginov.serbian.bot.data.manager.price

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.dto.price.PriceDescriptionDto
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.data.manager.shop.ShopDescriptionManager
import ru.loginov.serbian.bot.data.repository.price.PriceDescriptionRepository
import ru.loginov.serbian.bot.util.saveOr
import ru.loginov.serbian.bot.util.useSuspend
import java.time.LocalDateTime

@Component
class DefaultPriceDescriptionManager(
        private val categoryManager: CategoryManager,
        private val shopManager: ShopDescriptionManager,
        private val repo: PriceDescriptionRepository
) : PriceDescriptionManager {
    override suspend fun createOrUpdateForCategory(
            categoryId: Int,
            shopId: Int,
            minPrice: Float,
            maxPrice: Float
    ): PriceDescriptionDto? {
        if (!categoryManager.existsById(categoryId) || !shopManager.existsById(shopId)) {
            return null
        }

        val dto = repo.useSuspend {
            repo.findByCategoryIdAndShopId(categoryId, shopId).orElseGet {
                PriceDescriptionDto().also {
                    it.createdDateTime = LocalDateTime.now()
                }
            }
        }

        dto.lastUpdatedDateTime = LocalDateTime.now()

        val realMinPrice = if (minPrice <= maxPrice) minPrice else maxPrice
        val realMaxPrice = if (maxPrice >= minPrice) maxPrice else minPrice

        dto.minPrice = dto.minPrice?.coerceAtMost(realMinPrice) ?: realMinPrice
        dto.maxPrice = dto.maxPrice?.coerceAtLeast(realMaxPrice) ?: realMaxPrice

        return repo.useSuspend {
            it.saveOr(dto) { e ->
                LOGGER.warn("Can not save price description", e)
                null
            }
        }
    }

    override suspend fun createOrUpdateForProduct(
            productId: Int,
            shopId: Int,
            minPrice: Float,
            maxPrice: Float
    ): PriceDescriptionDto? {
        TODO("Not yet implemented")
    }

    override suspend fun findByCategory(categoryId: Int): List<PriceDescriptionDto> = repo.useSuspend {
        repo.findAllByCategoryId(categoryId)
    }

    override suspend fun findByCategory(categoryId: Int, shopId: Int): PriceDescriptionDto? = repo.useSuspend {
        it.findByCategoryIdAndShopId(categoryId, shopId).orElse(null)
    }

    override suspend fun findByProduct(productId: Int): List<PriceDescriptionDto> = repo.useSuspend {
        it.findAllByProductId(productId)
    }

    override suspend fun findByProduct(productId: Int, shopId: Int): PriceDescriptionDto? = repo.useSuspend {
        it.findByProductIdAndShopId(productId, shopId).orElse(null)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultPriceDescriptionManager::class.java)
    }
}