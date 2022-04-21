package ru.loginov.serbian.bot.data.repository.category

import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dao.category.CategoryDaoLocalization
import ru.loginov.serbian.bot.data.repository.search.AbstractSearchRepository

@Repository("categoryDaoLocalization")
class CategoryDaoLocalizationSearchRepository : AbstractSearchRepository<CategoryDaoLocalization>() {
    override val generalPropertyName: String = "name"
    override val entityClass: Class<CategoryDaoLocalization> = CategoryDaoLocalization::class.java
}