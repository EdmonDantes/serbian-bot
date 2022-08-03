package ru.loginov.serbian.bot.data.repository.purchase

import org.springframework.data.jpa.repository.JpaRepository
import ru.loginov.serbian.bot.data.dto.purchase.PurchaseDescription
import java.time.LocalDateTime

interface PurchaseDescriptionRepository : JpaRepository<PurchaseDescription, Int> {

    fun findAllByCategoryIdAndShopIdAndCreatedDateTimeAfter(
            categoryId: Int,
            shopId: Int,
            startDate: LocalDateTime
    ): List<PurchaseDescription>

    fun findTop20ByCategoryIdAndShopId(categoryId: Int, shopId: Int) : List<PurchaseDescription>

    fun findAllByProductIdAndShopIdAndCreatedDateTimeAfter(
            categoryId: Int,
            shopId: Int,
            startDate: LocalDateTime
    ): List<PurchaseDescription>

    fun findTop20ByProductIdAndShopId(categoryId: Int, shopId: Int) : List<PurchaseDescription>

}