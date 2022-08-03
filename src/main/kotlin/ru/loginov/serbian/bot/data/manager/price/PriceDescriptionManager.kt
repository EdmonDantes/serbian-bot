package ru.loginov.serbian.bot.data.manager.price

import ru.loginov.serbian.bot.data.dto.price.PriceDescription

interface PriceDescriptionManager {

    suspend fun findAllForCategory(categoryId: Int): List<PriceDescription>
    suspend fun findAllForShop(shopId: Int): List<PriceDescription>
    suspend fun findAllForProduct(productId: Int): List<PriceDescription>

    suspend fun findForCategory(categoryId: Int): PriceDescription?
    suspend fun findForShop(shopId: Int): PriceDescription?
    suspend fun findForProduct(productId: Int): PriceDescription?

    suspend fun findForShopAndCategory(shopId: Int, categoryId: Int): PriceDescription?
    suspend fun findForShopAndProduct(shopId: Int, productId: Int): PriceDescription?

}