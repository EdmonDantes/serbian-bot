package ru.loginov.serbian.bot.data.repository.category

import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dao.category.CategoryDaoLocalization
import ru.loginov.serbian.bot.data.dao.category.SubCategoryDaoLocalization
import ru.loginov.serbian.bot.data.repository.search.AbstractSearchRepository

@Repository("subCategoryDaoLocalization")
class SubCategoryDaoLocalizationSearchRepository : AbstractSearchRepository<SubCategoryDaoLocalization>() {
    override val generalPropertyName: String = "name"
    override val entityClass: Class<SubCategoryDaoLocalization> = SubCategoryDaoLocalization::class.java
}