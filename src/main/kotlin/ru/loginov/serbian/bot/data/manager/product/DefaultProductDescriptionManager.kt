package ru.loginov.serbian.bot.data.manager.product

import io.github.edmondantes.simple.localization.Localizer
import io.github.edmondantes.simple.localization.exception.LanguageNotSupportedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hibernate.Hibernate
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.dto.product.ProductDescription
import ru.loginov.serbian.bot.data.dto.product.ProductLocalizedName
import ru.loginov.serbian.bot.data.manager.category.CategoryManager
import ru.loginov.serbian.bot.data.repository.product.ProductDescriptionRepository
import ru.loginov.serbian.bot.data.repository.product.ProductLocalizedNameRepository
import ru.loginov.serbian.bot.data.repository.search.SearchRepository

//TODO: Create new business logic
@Service
class DefaultProductDescriptionManager(
        private val productDescriptionRepository: ProductDescriptionRepository,
        private val productLocalizedNameRepository: ProductLocalizedNameRepository,
        private val searchRepository: SearchRepository<ProductLocalizedName>,
        private val categoryManager: CategoryManager,
        private val localizer: Localizer
) : ProductDescriptionManager {
    override suspend fun create(names: Map<String, String>, categoryId: Int?): ProductDescription? {
        if (categoryId != null && !categoryManager.existsById(categoryId)) {
            return null
        }

        val dto = ProductDescription(null, categoryId, names.mapValues { (lang, name) ->
            if (!localizer.isSupport(lang)) {
                throw LanguageNotSupportedException(lang)
            }

            ProductLocalizedName(null as Int?, lang, name)
        })

        return withContext(Dispatchers.IO) {
            try {
                productDescriptionRepository.save(dto)
            } catch (e: Exception) {
                LOGGER.warn("Can not save product description with names '$names' and category id '$categoryId'", e)
                null
            }
        }
    }

    override suspend fun findById(id: Int): ProductDescription? =
            withContext(Dispatchers.IO) {
                try {
                    productDescriptionRepository.findByIdOrNull(id)
                } catch (e: Exception) {
                    LOGGER.warn("Can not find ProductDescriptionDto by id '$id'", e)
                    null
                }
            }

    override suspend fun findByCategoryId(categoryId: Int): List<ProductDescription> =
            withContext(Dispatchers.IO) {
                try {
                    productDescriptionRepository.findAllByCategoryIdWithLocalization(categoryId)
                } catch (e: Exception) {
                    LOGGER.warn("Can not find all ProductDescriptionDto by category id '$categoryId'", e)
                    emptyList()
                }
            }

    override suspend fun findByName(name: String): List<ProductLocalizedName> =
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
                    productDescriptionRepository.existsById(id)
                } catch (e: Exception) {
                    LOGGER.warn("Can not check existing for ProductDescriptionDto with id '$id'", e)
                    false
                }
            }

    override fun findLocalizedNameFor(dto: ProductDescription, language: String?): String? {
        if (language != null && !localizer.isSupport(language)) {
            throw LanguageNotSupportedException(language)
        }

        val lang = language ?: localizer.defaultLanguage

        val localizations = if (Hibernate.isInitialized(dto.localization)) {
            dto.localization
        } else if (dto.id != null) {
            productDescriptionRepository.findByIdWithLocalization(dto.id!!).orElse(null)?.localization
        } else {
            null
        }

        if (localizations == null) {
            error("Can not get localizations for product with id '${dto.id}'")
        }

        if (!Hibernate.isInitialized(localizations)) {
            error("Can not get initialized localizations for product with id '${dto.id}'")
        }

        val localization = localizations[lang]
                ?: localizations[localizer.defaultLanguage]
                ?: localizations.values.firstOrNull()

        return localization?.name
    }

    override suspend fun remove(id: Int): Boolean =
            withContext(Dispatchers.IO) {
                try {
                    productDescriptionRepository.deleteById(id)
                    true
                } catch (e: Exception) {
                    LOGGER.warn("Can not remove ProductDescriptionDto with id '$id'", e)
                    false
                }
            }

    override suspend fun changeLocalization(productId: Int, language: String, value: String): Boolean {
        if (!containsWithId(productId) || !localizer.isSupport(language) || value.isBlank()) {
            return false
        }

        val dto = ProductLocalizedName()
        dto.localizedId.id = productId
        dto.localizedId.locale = language
        dto.name = value

        return withContext(Dispatchers.IO) {
            try {
                productLocalizedNameRepository.save(dto)
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