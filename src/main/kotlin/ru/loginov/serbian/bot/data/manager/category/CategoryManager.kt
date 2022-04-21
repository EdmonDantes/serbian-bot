package ru.loginov.serbian.bot.data.manager.category

import ru.loginov.serbian.bot.data.dao.category.CategoryDao
import ru.loginov.serbian.bot.data.dao.category.CategoryDaoLocalization

interface CategoryManager {

    fun getAllCategories(locale: String): List<String>
    fun findCategoryByName(name: String): List<CategoryDaoLocalization>
    fun createNewCategory(names: Map<String, String>) : CategoryDao

}