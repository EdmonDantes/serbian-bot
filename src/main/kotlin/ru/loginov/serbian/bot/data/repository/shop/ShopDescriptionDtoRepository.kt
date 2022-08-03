package ru.loginov.serbian.bot.data.repository.shop

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.loginov.serbian.bot.data.dto.shop.ShopDescription

interface ShopDescriptionDtoRepository : JpaRepository<ShopDescription, Int> {
    @Query("SELECT dto FROM ShopDescription dto ORDER BY ABS(dto.latitude - :latitude) ASC, ABS(dto.longitude - :longitude) ASC")
    fun findTopByLocation(pageable: Pageable, latitude: Double, longitude: Double): List<ShopDescription>
}