package ru.loginov.serbian.bot.data.repository.shop

import org.springframework.data.jpa.repository.JpaRepository
import ru.loginov.serbian.bot.data.dto.shop.ShopComment
import java.time.LocalDateTime

interface ShopDescriptionCommentRepository : JpaRepository<ShopComment, Int> {

    fun findTop10ByShopIdAndCreatedTimeBeforeOrderByCreatedTimeDesc(
            entityId: Int,
            createdTime: LocalDateTime
    ): List<ShopComment>

}