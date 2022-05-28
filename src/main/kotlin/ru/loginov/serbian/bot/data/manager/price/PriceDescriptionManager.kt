package ru.loginov.serbian.bot.data.manager.price

import ru.loginov.serbian.bot.data.dto.price.PriceDescriptionDto

interface PriceDescriptionManager {

    suspend fun createOrUpdateForCategory(
            categoryId: Int,
            shopId: Int,
            minPrice: Float,
            maxPrice: Float
    ): PriceDescriptionDto?

    suspend fun createOrUpdateForProduct(
            productId: Int,
            shopId: Int,
            minPrice: Float,
            maxPrice: Float
    ): PriceDescriptionDto?

    suspend fun findByCategory(categoryId: Int): List<PriceDescriptionDto>
    suspend fun findByCategory(categoryId: Int, shopId: Int): PriceDescriptionDto?

    suspend fun findByProduct(productId: Int): List<PriceDescriptionDto>
    suspend fun findByProduct(productId: Int, shopId: Int): PriceDescriptionDto?


}