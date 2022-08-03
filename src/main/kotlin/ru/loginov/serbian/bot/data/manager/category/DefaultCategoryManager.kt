package ru.loginov.serbian.bot.data.manager.category

import io.github.edmondantes.simple.localization.Localizer
import io.github.edmondantes.simple.localization.exception.LanguageNotSupportedException
import org.hibernate.Hibernate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.dto.category.CategoryDescription
import ru.loginov.serbian.bot.data.dto.category.CategoryLocalizedName
import ru.loginov.serbian.bot.data.repository.category.CategoryDtoLocalizationRepository
import ru.loginov.serbian.bot.data.repository.category.CategoryDtoRepository
import ru.loginov.serbian.bot.data.repository.search.SearchRepository

@Service
class DefaultCategoryManager(
        private val categoryDtoRepository: CategoryDtoRepository,
        private val categoryDtoLocalizationRepository: CategoryDtoLocalizationRepository,
        private val categoryLocalizedNameSearchRepository: SearchRepository<CategoryLocalizedName>,
        private val localizer: Localizer
) : CategoryManager {

    override fun getAllRootCategories(): List<CategoryDescription> = categoryDtoRepository.findAllRootWithLocalization()

    override fun findByName(name: String): List<CategoryLocalizedName> =
            categoryLocalizedNameSearchRepository.findAllByGeneralProperty(name)

    override fun findById(categoryId: Int): CategoryDescription? =
            categoryDtoRepository.findByIdWithAllFields(categoryId).orElse(null)

    override fun existsById(categoryId: Int): Boolean =
            categoryDtoRepository.existsById(categoryId)

    override fun findLocalizedNameFor(categoryDescription: CategoryDescription, language: String?): String? {
        val localizations = if (Hibernate.isInitialized(categoryDescription.localization)) {
            categoryDescription.localization
        } else if (categoryDescription.id != null) {
            categoryDtoRepository.findByIdWithLocalization(categoryDescription.id!!).orElse(null)?.localization
        } else {
            null
        }

        if (localizations == null) {
            error("Can not get localizations for category with id '${categoryDescription.id}'")
        }

        if (!Hibernate.isInitialized(localizations)) {
            error("Can not get initialized localizations for category with id '${categoryDescription.id}'")
        }

        val localization = localizations[language ?: localizer.defaultLanguage]
                ?: localizations[localizer.defaultLanguage]
                ?: localizations.values.firstOrNull()

        return localization?.name
    }

    override fun create(names: Map<String, String>, parentId: Int?): CategoryDescription? {
        val category = CategoryDescription(parentId, null)

        names.forEach {
            if (localizer.isSupport(it.key)) {
                category.putLocalization(it.key, it.value)
            } else {
                throw LanguageNotSupportedException(it.key)
            }
        }

        return try {
            categoryDtoRepository.save(category)
        } catch (e: Exception) {
            LOGGER.warn("Can not save category dao: '$category'", e)
            null
        }
    }

    override fun changeLocalization(categoryId: Int, language: String, value: String): Boolean {
        if (!localizer.isSupport(language)) {
            throw LanguageNotSupportedException(language)
        }

        if (!categoryDtoRepository.existsById(categoryId)) {
            return false
        }

        val localization = CategoryLocalizedName(categoryId, language, value)
        return try {
            categoryDtoLocalizationRepository.save(localization)
            true
        } catch (e: Exception) {
            LOGGER.warn("Can not save localization for category with id '$categoryId' and language '$language': '$value'")
            false
        }
    }

    override fun remove(categoryId: Int): Boolean =
            try {
                categoryDtoRepository.deleteById(categoryId)
                true
            } catch (e: Exception) {
                LOGGER.warn("Can not remove category with id '$categoryId'", e)
                false
            }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultCategoryManager::class.java)
    }

}