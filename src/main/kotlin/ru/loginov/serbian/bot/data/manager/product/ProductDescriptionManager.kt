package ru.loginov.serbian.bot.data.manager.product

import ru.loginov.serbian.bot.data.dto.product.ProductDescriptionDto
import ru.loginov.serbian.bot.data.dto.product.ProductDescriptionDtoLocalization
import ru.loginov.simple.localization.exception.LanguageNotSupportedException

//TODO: Create new business logic
interface ProductDescriptionManager {

    suspend fun create(names: Map<String, String>, categoryId: Int?): ProductDescriptionDto?

    suspend fun findById(id: Int): ProductDescriptionDto?
    suspend fun findByCategoryId(categoryId: Int): List<ProductDescriptionDto>
    suspend fun findByName(name: String): List<ProductDescriptionDtoLocalization>
    suspend fun containsWithId(id: Int): Boolean

    /**
     * Try to get localized name for [dto]. If necessary manager will load additional data
     * @param language language for localization. Default: 'en' (English)
     * @return If it can, return localized name for a given language,
     * else for English language,
     * else for any language, else null
     * @throws LanguageNotSupportedException if [language] is not support
     * @throws IllegalStateException if manager can not load localizations
     */
    @Throws(LanguageNotSupportedException::class, IllegalStateException::class)
    fun findLocalizedNameFor(dto: ProductDescriptionDto, language: String?): String?

    suspend fun remove(id: Int): Boolean

    @Throws(LanguageNotSupportedException::class)
    suspend fun changeLocalization(productId: Int, language: String, value: String): Boolean
}