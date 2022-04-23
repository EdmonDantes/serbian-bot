package ru.loginov.serbian.bot.data.manager.category

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.dto.category.CategoryDto
import ru.loginov.serbian.bot.data.dto.category.CategoryDtoLocalization
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.data.repository.category.CategoryDtoLocalizationRepository
import ru.loginov.serbian.bot.data.repository.category.CategoryDtoRepository
import ru.loginov.serbian.bot.data.repository.search.SearchRepository

@Service
class DefaultCategoryManager : CategoryManager {

    @Autowired
    private lateinit var categoryDtoRepository: CategoryDtoRepository

    @Autowired
    private lateinit var categoryDtoLocalizationRepository: CategoryDtoLocalizationRepository

    @Autowired
    private lateinit var categoryDtoLocalizationSearchRepository: SearchRepository<CategoryDtoLocalization>

    @Autowired
    private lateinit var localizationManager: LocalizationManager

    override fun getAllCategories(locale: String): List<String> = categoryDtoRepository
            .findAllWithLocalization()
            .mapNotNull {
                it.localization[locale] ?: it.localization[localizationManager.defaultLanguage] ?: it.localization.values.first()
            }.mapNotNull {
                it.name
            }

    override fun findCategoryByName(name: String): List<CategoryDtoLocalization> =
            categoryDtoLocalizationSearchRepository.findAllByGeneralProperty(name)

    override fun createNewCategory(names: Map<String, String>): CategoryDto {
        val notSupportLang = names.keys.filter { !localizationManager.languageIsSupport(it) }
        if (notSupportLang.isNotEmpty()) {
            throw IllegalArgumentException("Not support language: '$notSupportLang'")
        }

        val category =  CategoryDto()
        names.forEach { category.putLocalization(it.key, it.value) }
        try {
            return categoryDtoRepository.save(category)
        } catch (e: Exception) {
            throw IllegalStateException("Can not save category dao: '$category'", e)
        }
    }

}