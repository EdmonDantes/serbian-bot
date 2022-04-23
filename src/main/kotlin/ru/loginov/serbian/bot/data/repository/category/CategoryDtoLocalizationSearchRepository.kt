package ru.loginov.serbian.bot.data.repository.category

import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.category.CategoryDtoLocalization
import ru.loginov.serbian.bot.data.repository.search.AbstractSearchRepository

@Repository("categoryDaoLocalization")
class CategoryDtoLocalizationSearchRepository : AbstractSearchRepository<CategoryDtoLocalization>() {
    override val generalPropertyName: String = "name"
    override val entityClass: Class<CategoryDtoLocalization> = CategoryDtoLocalization::class.java
}