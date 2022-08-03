package ru.loginov.serbian.bot.data.manager.shop

import ru.loginov.serbian.bot.data.dto.shop.ShopComment
import ru.loginov.serbian.bot.data.dto.shop.ShopDescription
import java.time.LocalDateTime

interface ShopDescriptionManager {

    suspend fun create(googleMapLink: String, floor: Int? = null): ShopDescription?
    suspend fun create(
            name: String,
            address: String,
            floor: Int? = null,
            latitude: Double? = null,
            longitude: Double? = null,
    ): ShopDescription?

    suspend fun findById(id: Int): ShopDescription?
    suspend fun findByName(name: String): List<ShopDescription>
    suspend fun findNearest(latitude: Double, longitude: Double): List<ShopDescription>
    suspend fun existsById(id: Int): Boolean

    suspend fun remove(id: Int): Boolean

    suspend fun addComment(shopId: Int, comment: String): Boolean
    suspend fun getComments(shopId: Int, beforeDate: LocalDateTime? = null): List<ShopComment>

}