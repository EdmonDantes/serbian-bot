package ru.loginov.serbian.bot.data.repository.price

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.price.PriceDescriptionDto
import java.util.Optional

@Repository
interface PriceDescriptionRepository : JpaRepository<PriceDescriptionDto, Int> {

    fun findAllByCategoryId(categoryId: Int): List<PriceDescriptionDto>
    fun findByCategoryIdAndShopId(categoryId: Int, shopId: Int): Optional<PriceDescriptionDto>

    fun findAllByProductId(productId: Int): List<PriceDescriptionDto>
    fun findByProductIdAndShopId(productId: Int, shopId: Int): Optional<PriceDescriptionDto>
}