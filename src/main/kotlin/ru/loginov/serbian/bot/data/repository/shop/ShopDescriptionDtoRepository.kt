package ru.loginov.serbian.bot.data.repository.shop

import org.springframework.data.jpa.repository.JpaRepository
import ru.loginov.serbian.bot.data.dto.shop.ShopDescriptionDto

interface ShopDescriptionDtoRepository : JpaRepository<ShopDescriptionDto, String> {

}