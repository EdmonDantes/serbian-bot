package ru.loginov.serbian.bot.data.repository.purchase

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.purchase.PurchaseDescription

@Repository
interface PurchaseDescriptionRepository : JpaRepository<PurchaseDescription, Int> {

//    fun findAllByCategoryIdAndShopIdAndCreatedDateTimeAfter(
//            categoryId: Int,
//            shopId: Int,
//            startDate: LocalDateTime
//    ): List<PurchaseDescription>
//
//    fun findTop20ByCategoryIdAndShopId(categoryId: Int, shopId: Int) : List<PurchaseDescription>
//
//    fun findAllByProductIdAndShopIdAndCreatedDateTimeAfter(
//            productId: Int,
//            shopId: Int,
//            startDate: LocalDateTime
//    ): List<PurchaseDescription>
//
//    fun findTop20ByProductIdAndShopId(productId: Int, shopId: Int) : List<PurchaseDescription>

}