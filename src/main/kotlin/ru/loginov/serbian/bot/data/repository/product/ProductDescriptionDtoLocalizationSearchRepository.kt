package ru.loginov.serbian.bot.data.repository.product

import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.product.ProductDescriptionDtoLocalization
import ru.loginov.serbian.bot.data.repository.search.AbstractSearchRepository

@Repository("productDescriptionDaoLocalization")
class ProductDescriptionDtoLocalizationSearchRepository : AbstractSearchRepository<ProductDescriptionDtoLocalization>() {
    override val generalPropertyName: String = "name"
    override val entityClass: Class<ProductDescriptionDtoLocalization> = ProductDescriptionDtoLocalization::class.java
}