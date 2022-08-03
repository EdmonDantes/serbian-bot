package ru.loginov.serbian.bot.data.manager.category

import io.github.edmondantes.simple.localization.exception.LanguageNotSupportedException
import ru.loginov.serbian.bot.data.dto.category.CategoryDescription
import ru.loginov.serbian.bot.data.dto.category.CategoryLocalizedName

/**
 * Class for managing [CategoryDescription]
 */
interface CategoryManager {

    /**
     * Try to find all categories without parent.
     * @return List of found categories else empty list
     */
    fun getAllRootCategories(): List<CategoryDescription>

    /**
     * Try to find category by [name] with most match chance.
     * @return List of found categories names with sorted by match chance descending.
     */
    fun findByName(name: String): List<CategoryLocalizedName>

    /**
     * Try to find category with [categoryId]. Load additional data for category
     *
     * If you want only check existing please use method [existsById]
     * @return If found return [CategoryDescription] else null
     */
    fun findById(categoryId: Int): CategoryDescription?

    /**
     * Check if application have category with [categoryId]
     * @return True if application have category, else false
     */
    fun existsById(categoryId: Int): Boolean

    /**
     * Try to get localized name for [categoryDescription]. If necessary manager will load additional data
     * @param language language for localization. Default: 'en' (English)
     * @return If it can, return localized name for a given language,
     * else for English language,
     * else for any language, else null
     * @throws LanguageNotSupportedException if [language] is not support
     * @throws IllegalStateException if manager can not load localizations
     */
    @Throws(LanguageNotSupportedException::class, IllegalStateException::class)
    fun findLocalizedNameFor(categoryDescription: CategoryDescription, language: String?): String?

    /**
     * Create new category with [names] and parent with id [parentId]
     * @param names keys - languages, values - localized names
     * @return If success saved object of [CategoryDescription], else null
     * @throws LanguageNotSupportedException If one language in [names] is not supported
     */
    fun create(names: Map<String, String>, parentId: Int? = null): CategoryDescription?

    /**
     * Change localization name for category with [categoryId]
     * @return True if it was changed successfully, else false
     * @throws LanguageNotSupportedException If [language] is not support
     */
    @Throws(LanguageNotSupportedException::class)
    fun changeLocalization(categoryId: Int, language: String, value: String): Boolean

    /**
     * Remove category with [categoryId]
     * @return True if it was removed successfully, else false
     */
    fun remove(categoryId: Int): Boolean

}