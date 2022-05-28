package ru.loginov.serbian.bot.data.manager.product

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.dto.product.ProductDescriptionDto
import ru.loginov.serbian.bot.data.dto.product.ProductDescriptionDtoLocalization
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.data.manager.localization.LocalizationManager
import ru.loginov.serbian.bot.data.manager.localization.exception.LanguageNotSupportedException
import ru.loginov.serbian.bot.data.repository.product.ProductDescriptionDtoLocalizationRepository
import ru.loginov.serbian.bot.data.repository.product.ProductDescriptionDtoRepository
import ru.loginov.serbian.bot.data.repository.search.SearchRepository

@Service
class DefaultProductDescriptionManager(
        private val productDescriptionDtoRepository: ProductDescriptionDtoRepository,
        private val productDescriptionDtoLocalizationRepository: ProductDescriptionDtoLocalizationRepository,
        private val searchRepository: SearchRepository<ProductDescriptionDtoLocalization>,
        private val categoryManager: CategoryManager,
        private val localizationManager: LocalizationManager
) : ProductDescriptionManager {
    override suspend fun create(names: Map<String, String>, categoryId: Int?): ProductDescriptionDto? {
        if (categoryId != null && !categoryManager.existsById(categoryId)) {
            return null
        }

        val dto = ProductDescriptionDto()
        dto.categoryId = categoryId
        dto.localization = HashMap()
        names.forEach { (lang, name) ->
            if (!localizationManager.isSupport(lang)) {
                throw LanguageNotSupportedException(lang)
            }

            val localization = ProductDescriptionDtoLocalization()
            localization.entity = dto
            localization.name = name
            localization.localizedId.locale = lang

            dto.localization[lang] = localization
        }

        return withContext(Dispatchers.IO) {
            try {
                productDescriptionDtoRepository.save(dto)
            } catch (e: Exception) {
                LOGGER.warn("Can not save product description with names '$names' and category id '$categoryId'", e)
                null
            }
        }
    }

    override suspend fun findById(id: Int): ProductDescriptionDto? =
            withContext(Dispatchers.IO) {
                try {
                    productDescriptionDtoRepository.findByIdOrNull(id)
                } catch (e: Exception) {
                    LOGGER.warn("Can not find ProductDescriptionDto by id '$id'", e)
                    null
                }
            }

    override suspend fun findByCategoryId(categoryId: Int): List<ProductDescriptionDto> =
            withContext(Dispatchers.IO) {
                try {
                    productDescriptionDtoRepository.findAllByCategoryIdWithLocalization(categoryId)
                } catch (e: Exception) {
                    LOGGER.warn("Can not find all ProductDescriptionDto by category id '$categoryId'", e)
                    emptyList()
                }
            }

    override suspend fun findByName(name: String): List<ProductDescriptionDtoLocalization> =
            withContext(Dispatchers.IO) {
                try {
                    searchRepository.findAllByGeneralProperty(name)
                } catch (e: Exception) {
                    LOGGER.warn("Can not find ProductDescriptionDto by name '$name'", e)
                    emptyList()
                }
            }


    override suspend fun containsWithId(id: Int): Boolean =
            withContext(Dispatchers.IO) {
                try {
                    productDescriptionDtoRepository.existsById(id)
                } catch (e: Exception) {
                    LOGGER.warn("Can not check existing for ProductDescriptionDto with id '$id'", e)
                    false
                }
            }

    override suspend fun remove(id: Int): Boolean =
            withContext(Dispatchers.IO) {
                try {
                    productDescriptionDtoRepository.deleteById(id)
                    true
                } catch (e: Exception) {
                    LOGGER.warn("Can not remove ProductDescriptionDto with id '$id'", e)
                    false
                }
            }

    override suspend fun changeLocalization(productId: Int, language: String, value: String): Boolean {
        if (!containsWithId(productId) || !localizationManager.isSupport(language) || value.isBlank()) {
            return false
        }

        val dto = ProductDescriptionDtoLocalization()
        dto.localizedId.id = productId
        dto.localizedId.locale = language
        dto.name = value

        return withContext(Dispatchers.IO) {
            try {
                productDescriptionDtoLocalizationRepository.save(dto)
                true
            } catch (e: Exception) {
                LOGGER.warn(
                        "Can not save new localization for product with id '$productId', for language '$language' with value '$value'",
                        e
                )
                false
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultProductDescriptionManager::class.java)
    }
}