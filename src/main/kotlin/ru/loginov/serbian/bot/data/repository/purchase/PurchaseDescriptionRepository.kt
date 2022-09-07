package ru.loginov.serbian.bot.data.repository.purchase

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.purchase.PurchaseDescription
import java.time.LocalDateTime

@Repository
interface PurchaseDescriptionRepository : JpaRepository<PurchaseDescription, Int> {


    fun findAllByCategoryIdAndShopIdAndCreatedDateTimeAfter(
            categoryId: Int,
            shopId: Int,
            startDate: LocalDateTime,
            pageable: Pageable? = null
    ): Page<PurchaseDescription>

    fun findAllByProductIdAndShopIdAndCreatedDateTimeAfter(
            productId: Int,
            shopId: Int,
            startDate: LocalDateTime,
            pageable: Pageable? = null
    ): Page<PurchaseDescription>


    fun findAllByShopIdAndCreatedDateTimeAfter(
            shopId: Int,
            startDate: LocalDateTime,
            pageable: Pageable? = null
    ): Page<PurchaseDescription>


//
//    fun findTop20ByCategoryIdAndShopId(categoryId: Int, shopId: Int) : List<PurchaseDescription>
//

//
//    fun findTop20ByProductIdAndShopId(productId: Int, shopId: Int) : List<PurchaseDescription>

}