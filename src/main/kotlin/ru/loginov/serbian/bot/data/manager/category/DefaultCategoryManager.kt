package ru.loginov.serbian.bot.data.manager.category

import org.hibernate.Hibernate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.dto.category.CategoryDto
import ru.loginov.serbian.bot.data.dto.category.CategoryDtoLocalization
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.data.manager.localization.exception.LanguageNotSupportedException
import ru.loginov.serbian.bot.data.repository.category.CategoryDtoLocalizationRepository
import ru.loginov.serbian.bot.data.repository.category.CategoryDtoRepository
import ru.loginov.serbian.bot.data.repository.search.SearchRepository

@Service
class DefaultCategoryManager(
        private val categoryDtoRepository: CategoryDtoRepository,
        private val categoryDtoLocalizationRepository: CategoryDtoLocalizationRepository,
        private val categoryDtoLocalizationSearchRepository: SearchRepository<CategoryDtoLocalization>,
        private val localizationManager: LocalizationManager
) : CategoryManager {

    override fun getAllRootCategories(): List<CategoryDto> = categoryDtoRepository.findAllRootWithLocalization()

    override fun findByName(name: String): List<CategoryDtoLocalization> =
            categoryDtoLocalizationSearchRepository.findAllByGeneralProperty(name)

    override fun findById(categoryId: Int): CategoryDto? =
            categoryDtoRepository.findByIdWithAllFields(categoryId).orElse(null)

    override fun existsById(categoryId: Int): Boolean =
            categoryDtoRepository.existsById(categoryId)

    override fun findLocalizedNameFor(categoryDto: CategoryDto, language: String?): String? {
        if (language != null && !localizationManager.isSupport(language)) {
            throw LanguageNotSupportedException(language)
        }

        val lang = language ?: localizationManager.defaultLanguage

        val localizations = if (Hibernate.isInitialized(categoryDto.localization)) {
            categoryDto.localization
        } else if (categoryDto.id != null) {
            categoryDtoRepository.findByIdWithLocalization(categoryDto.id!!).orElse(null)?.localization
        } else {
            null
        }

        if (localizations == null) {
            error("Can not get localizations for category with id '${categoryDto.id}'")
        }

        if (!Hibernate.isInitialized(localizations)) {
            error("Can not get initialized localizations for category with id '${categoryDto.id}'")
        }

        val localization = localizations[lang]
                ?: localizations[localizationManager.defaultLanguage]
                ?: localizations.values.firstOrNull()

        return localization?.name
    }

    override fun create(names: Map<String, String>, parentId: Int?): CategoryDto? {
        val category = CategoryDto(parentId, null)

        names.forEach {
            if (localizationManager.isSupport(it.key)) {
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
        if (!localizationManager.isSupport(language)) {
            throw LanguageNotSupportedException(language)
        }

        if (!categoryDtoRepository.existsById(categoryId)) {
            return false
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