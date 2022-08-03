package ru.loginov.serbian.bot.data.repository.price

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.price.PriceDescription
import java.util.Optional

@Repository
interface PriceDescriptionRepository : JpaRepository<PriceDescription, Int> {

    fun findAllByCategoryId(categoryId: Int): List<PriceDescription>
    fun findAllByShopId(shopId: Int): List<PriceDescription>
    fun findAllByProductId(productId: Int): List<PriceDescription>

    fun findByCategoryIdAndShopIdAndProductId(
            categoryId: Int?,
            shopId: Int?,
            productId: Int?
    ): Optional<PriceDescription>

}