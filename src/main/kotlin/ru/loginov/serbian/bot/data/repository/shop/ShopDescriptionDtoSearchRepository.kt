package ru.loginov.serbian.bot.data.repository.shop

import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.shop.ShopDescriptionDto
import ru.loginov.serbian.bot.data.repository.search.AbstractSearchRepository

@Repository("searchDescriptionDtoRepository")
class ShopDescriptionDtoSearchRepository : AbstractSearchRepository<ShopDescriptionDto>() {
    override val generalPropertyName: String = "shopName"
    override val entityClass: Class<ShopDescriptionDto> = ShopDescriptionDto::class.java
}