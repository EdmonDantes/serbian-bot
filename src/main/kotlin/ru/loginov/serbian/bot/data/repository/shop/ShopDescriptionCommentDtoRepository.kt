package ru.loginov.serbian.bot.data.repository.shop

import org.springframework.data.jpa.repository.JpaRepository
import ru.loginov.serbian.bot.data.dto.shop.ShopDescriptionCommentDto
import java.time.LocalDateTime

interface ShopDescriptionCommentDtoRepository : JpaRepository<ShopDescriptionCommentDto, Int> {

    fun findTopByEntityIdAndCreatedTimeAfter(
            entityId: String,
            createdTime: LocalDateTime
    ): List<ShopDescriptionCommentDto>

}