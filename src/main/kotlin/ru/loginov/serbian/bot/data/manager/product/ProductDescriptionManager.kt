package ru.loginov.serbian.bot.data.manager.product

import ru.loginov.serbian.bot.data.dto.product.ProductDescriptionDto
import ru.loginov.serbian.bot.data.dto.product.ProductDescriptionDtoLocalization
import ru.loginov.serbian.bot.data.manager.localization.exception.LanguageNotSupportedException

interface ProductDescriptionManager {

    suspend fun create(names: Map<String, String>, categoryId: Int?): ProductDescriptionDto?

    suspend fun findById(id: Int): ProductDescriptionDto?
    suspend fun findByCategoryId(categoryId: Int): List<ProductDescriptionDto>
    suspend fun findByName(name: String): List<ProductDescriptionDtoLocalization>
    suspend fun containsWithId(id: Int): Boolean

    suspend fun remove(id: Int): Boolean

    @Throws(LanguageNotSupportedException::class)
    suspend fun changeLocalization(productId: Int, language: String, value: String): Boolean
}