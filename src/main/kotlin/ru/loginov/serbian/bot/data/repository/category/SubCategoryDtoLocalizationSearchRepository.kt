package ru.loginov.serbian.bot.data.repository.category

import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.category.SubCategoryDtoLocalization
import ru.loginov.serbian.bot.data.repository.search.AbstractSearchRepository

@Repository("subCategoryDaoLocalization")
class SubCategoryDtoLocalizationSearchRepository : AbstractSearchRepository<SubCategoryDtoLocalization>() {
    override val generalPropertyName: String = "name"
    override val entityClass: Class<SubCategoryDtoLocalization> = SubCategoryDtoLocalization::class.java
}