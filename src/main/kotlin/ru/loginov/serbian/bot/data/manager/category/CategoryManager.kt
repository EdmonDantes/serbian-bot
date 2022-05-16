package ru.loginov.serbian.bot.data.manager.category

import ru.loginov.serbian.bot.data.dto.category.CategoryDto
import ru.loginov.serbian.bot.data.dto.category.CategoryDtoLocalization

interface CategoryManager {

    fun getAllRootCategories(locale: String): List<String>

    fun findCategoriesByName(name: String): List<CategoryDtoLocalization>
    fun findCategoryById(categoryId: Int): CategoryDto?

    fun createNewCategory(names: Map<String, String>, parentId: Int? = null): CategoryDto
    fun changeLocalizationNameForCategory(categoryId: Int, language: String, value: String): Boolean

}