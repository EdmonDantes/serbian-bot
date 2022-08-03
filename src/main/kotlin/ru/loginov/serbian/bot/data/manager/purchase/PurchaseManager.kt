package ru.loginov.serbian.bot.data.manager.purchase

import ru.loginov.serbian.bot.data.dto.purchase.PurchaseDescription

interface PurchaseManager {

    suspend fun createForCategory(categoryId: Int, shopId: Int, price: Float) : PurchaseDescription?
    suspend fun createForProduct(productId: Int, shopId: Int, price: Float) : PurchaseDescription?

}