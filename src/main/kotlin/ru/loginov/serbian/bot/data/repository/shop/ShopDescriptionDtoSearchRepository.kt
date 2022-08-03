package ru.loginov.serbian.bot.data.repository.shop

import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.shop.ShopDescription
import ru.loginov.serbian.bot.data.repository.search.AbstractSearchRepository

@Repository("searchDescriptionDtoRepository")
class ShopDescriptionDtoSearchRepository : AbstractSearchRepository<ShopDescription>() {
    override val generalPropertyName: String = "shopName"
    override val entityClass: Class<ShopDescription> = ShopDescription::class.java
}