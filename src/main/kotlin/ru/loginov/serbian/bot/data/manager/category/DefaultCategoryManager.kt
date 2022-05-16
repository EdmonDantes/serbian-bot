package ru.loginov.serbian.bot.data.manager.category

import org.slf4j.LoggerFactory
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

    override fun getAllRootCategories(locale: String): List<String> = categoryDtoRepository
            .findAllRootWithLocalization()
            .mapNotNull {
                it.localization[locale]
                        ?: it.localization[localizationManager.defaultLanguage]
                        ?: it.localization.values.firstOrNull()
            }.mapNotNull {
                it.name
            }

    override fun findCategoriesByName(name: String): List<CategoryDtoLocalization> =
            categoryDtoLocalizationSearchRepository.findAllByGeneralProperty(name)

    override fun findCategoryById(categoryId: Int): CategoryDto? =
            categoryDtoRepository.findByIdWithAllFields(categoryId).orElse(null)

    override fun createNewCategory(names: Map<String, String>, parentId: Int?): CategoryDto {
        val notSupportLang = names.keys.filter { !localizationManager.languageIsSupport(it) }
        if (notSupportLang.isNotEmpty()) {
            throw IllegalArgumentException("Not support languages: '$notSupportLang'")
        }

        val category = CategoryDto()
        category.parentId = parentId
        names.forEach { category.putLocalization(it.key, it.value) }
        try {
            return categoryDtoRepository.save(category)
        } catch (e: Exception) {
            throw IllegalStateException("Can not save category dao: '$category'", e)
        }
    }

    override fun changeLocalizationNameForCategory(categoryId: Int, language: String, value: String): Boolean {
        if (!localizationManager.languageIsSupport(language)) {
            throw IllegalArgumentException("Not support language: '$language'")
        }

        val localization = CategoryDtoLocalization(categoryId, language, value)
        return try {
            categoryDtoLocalizationRepository.save(localization)
            true
        } catch (e: Exception) {
            LOGGER.warn("Can not save localization for category with id '$categoryId' and language '$language': '$value'")
            false
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultCategoryManager::class.java)
    }

}