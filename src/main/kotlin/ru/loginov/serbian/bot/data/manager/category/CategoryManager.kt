package ru.loginov.serbian.bot.data.manager.category

import ru.loginov.serbian.bot.data.dto.category.CategoryDto
import ru.loginov.serbian.bot.data.dto.category.CategoryDtoLocalization

interface CategoryManager {

    fun getAllCategories(locale: String): List<String>
    fun findCategoryByName(name: String): List<CategoryDtoLocalization>
    fun createNewCategory(names: Map<String, String>) : CategoryDto

}