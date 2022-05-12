package ru.loginov.serbian.bot.data.manager.category

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.loginov.serbian.bot.configuration.ExecutorsConfiguration
import ru.loginov.serbian.bot.configuration.InMemoryJdbcConfiguration
import ru.loginov.serbian.bot.data.dto.category.CategoryDto

@SpringBootTest(classes = [InMemoryJdbcConfiguration::class, ExecutorsConfiguration::class])
@ComponentScan("ru.loginov.data.manager.category")
@EnableJpaRepositories
class CategoryManagerTest {

    @Autowired
    private lateinit var manager: CategoryManager

    @Test
    fun testCreateRootCategory() {
        val language = "en"
        val expectedValue = "Test category"
        var categoryDto: CategoryDto? = manager.createNewCategory(mapOf(language to expectedValue), null)

        assertNotNull(categoryDto, "Category manager return null on creating new a root category")
        assertNotNull(categoryDto!!.id, "Category manager return new a root category with null in 'id' field")
        categoryDto = manager.findCategoryById(categoryDto.id!!)

        assertNotNull(categoryDto, "Category manager return null on finding category by id")
        assertNotNull(
                categoryDto!!.localization,
                "Category manager found a root category with null in 'localization' field"
        )
        assertTrue(
                categoryDto.localization!!.isNotEmpty(),
                "Category manager found a root category with empty 'localization' field"
        )
        assertTrue(
                categoryDto.localization.containsKey(language),
                "Category manager found a root category with no empty 'localization' field, but without language: '$language'"
        )
        assertEquals(
                expectedValue, categoryDto.localization[language]!!.name,
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