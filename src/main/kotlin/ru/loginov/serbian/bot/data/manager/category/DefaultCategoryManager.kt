package ru.loginov.serbian.bot.data.manager.category

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.dao.category.CategoryDao
import ru.loginov.serbian.bot.data.dao.category.CategoryDaoLocalization
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.data.repository.category.CategoryDaoLocalizationRepository
import ru.loginov.serbian.bot.data.repository.category.CategoryDaoRepository
import ru.loginov.serbian.bot.data.repository.search.SearchRepository

@Service
class DefaultCategoryManager : CategoryManager {

    @Autowired
    private lateinit var categoryDaoRepository: CategoryDaoRepository

    @Autowired
    private lateinit var categoryDaoLocalizationRepository: CategoryDaoLocalizationRepository

    @Autowired
    private lateinit var categoryDaoLocalizationSearchRepository: SearchRepository<CategoryDaoLocalization>

    @Autowired
    private lateinit var localizationManager: LocalizationManager

    override fun getAllCategories(locale: String): List<String> = categoryDaoRepository
            .findAllWithLocalization()
            .mapNotNull {
                it.localization[locale] ?: it.localization[localizationManager.defaultLanguage] ?: it.localization.values.first()
            }.mapNotNull {
                it.name
            }

    override fun findCategoryByName(name: String): List<CategoryDaoLocalization> =
            categoryDaoLocalizationSearchRepository.findAllByGeneralProperty(name)

    override fun createNewCategory(names: Map<String, String>): CategoryDao {
        val notSupportLang = names.keys.filter { !localizationManager.languageIsSupport(it) }
        if (notSupportLang.isNotEmpty()) {
            throw IllegalArgumentException("Not support language: '$notSupportLang'")
        }

        val category =  CategoryDao()
        names.forEach { category.putLocalization(it.key, it.value) }
        try {
            return categoryDaoRepository.save(category)
        } catch (e: Exception) {
            throw IllegalStateException("Can not save category dao: '$category'", e)
        }
    }

}