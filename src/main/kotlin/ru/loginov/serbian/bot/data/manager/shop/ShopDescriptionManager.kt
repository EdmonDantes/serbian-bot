package ru.loginov.serbian.bot.data.manager.shop

import ru.loginov.serbian.bot.data.dto.shop.ShopDescriptionCommentDto
import ru.loginov.serbian.bot.data.dto.shop.ShopDescriptionDto
import java.time.LocalDateTime

interface ShopDescriptionManager {

    suspend fun create(googleMapLink: String, floor: Int? = null): ShopDescriptionDto?
    suspend fun create(name: String, address: String, floor: Int? = null): ShopDescriptionDto?

    suspend fun findById(id: Int): ShopDescriptionDto?
    suspend fun findByName(name: String): List<ShopDescriptionDto>
    suspend fun existsById(id: Int): Boolean

    suspend fun remove(id: Int): Boolean

    suspend fun addComment(shopId: Int, comment: String): Boolean
    suspend fun getComments(shopId: Int, beforeDate: LocalDateTime? = null): List<ShopDescriptionCommentDto>

}