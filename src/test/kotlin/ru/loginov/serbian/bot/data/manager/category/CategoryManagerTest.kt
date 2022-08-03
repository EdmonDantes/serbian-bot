package ru.loginov.serbian.bot.data.manager.category

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.loginov.serbian.bot.data.dto.category.CategoryDescription


class CategoryManagerTest {

    @Autowired
    private lateinit var manager: CategoryManager

    @Test
    fun testCreateRootCategory() {
        val language = "en"
        val expectedValue = "Test category"
        var categoryDescription: CategoryDescription? = manager.create(mapOf(language to expectedValue), null)

        assertNotNull(categoryDescription, "Category manager return null on creating new a root category")
        assertNotNull(categoryDescription!!.id, "Category manager return new a root category with null in 'id' field")
        categoryDescription = manager.findById(categoryDescription.id!!)

        assertNotNull(categoryDescription, "Category manager return null on finding category by id")
        assertNotNull(
                categoryDescription!!.localization,
                "Category manager found a root category with null in 'localization' field"
        )
        assertTrue(
                categoryDescription.localization!!.isNotEmpty(),
                "Category manager found a root category with empty 'localization' field"
        )
        assertTrue(
                categoryDescription.localization.containsKey(language),
                "Category manager found a root category with no empty 'localization' field, but without language: '$language'"
        )
        assertEquals(
                expectedValue, categoryDescription.localization[language]!!.name,
                "Category manager found a root category, but value is wrong for language '$language'"
        )
    }

//    @Test
//    fun testCreateSubCategory() {
//        val language = "en"
//        val rootCategoryValue = "Root category"
//        val subCategoryValue = "Sub category"
//
//        val rootCategory = manager.
//
//    }

}