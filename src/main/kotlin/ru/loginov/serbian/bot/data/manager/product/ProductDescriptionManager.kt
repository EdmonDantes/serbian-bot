package ru.loginov.serbian.bot.data.manager.product

import io.github.edmondantes.simple.localization.exception.LanguageNotSupportedException
import ru.loginov.serbian.bot.data.dto.product.ProductDescription
import ru.loginov.serbian.bot.data.dto.product.ProductLocalizedName

//TODO: Create new business logic
interface ProductDescriptionManager {

    suspend fun create(names: Map<String, String>, categoryId: Int?): ProductDescription?

    suspend fun findById(id: Int): ProductDescription?
    suspend fun findByCategoryId(categoryId: Int): List<ProductDescription>
    suspend fun findByName(name: String): List<ProductLocalizedName>
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
    fun findLocalizedNameFor(dto: ProductDescription, language: String?): String?

    suspend fun remove(id: Int): Boolean

    @Throws(LanguageNotSupportedException::class)
    suspend fun changeLocalization(productId: Int, language: String, value: String): Boolean
}