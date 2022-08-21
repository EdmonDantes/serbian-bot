package ru.loginov.serbian.bot.data.repository.category

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.loginov.serbian.bot.data.dto.category.CategoryDescription
import ru.loginov.serbian.bot.data.dto.category.CategoryLocalizedName
import ru.loginov.serbian.bot.data.dto.localization.constructLocalization

@SpringBootTest(classes = [CategoryDescriptionRepository::class, CategoryLocalizedNameRepository::class])
@EnableJpaRepositories
@EnableAutoConfiguration
@EntityScan(basePackages = ["ru.loginov.serbian.bot.data.*"])
class CategoryDescriptionRepositoriesTest {

    @Autowired
    private lateinit var repository: CategoryDescriptionRepository

    @Autowired
    private lateinit var localizationRepository: CategoryLocalizedNameRepository

    @BeforeEach
    fun beforeTest() {
        repository.deleteAll()
        localizationRepository.deleteAll()
    }

    @Test
    fun simpleTest() {
        repository.saveAndFlush(CategoryDescription())
        Assertions.assertEquals(1, repository.count())
    }

    @Test
    fun createChildTest() {
        val category = repository.saveAndFlush(CategoryDescription())

        Assertions.assertNotNull(category)
        Assertions.assertNotNull(category.id)

        val child = repository.saveAndFlush(CategoryDescription(parent = CategoryDescription(category.id)))

        val fetchCategory = repository.findByIdWithAllFields(category.id!!).orElse(null)
        Assertions.assertNotNull(fetchCategory)
        Assertions.assertEquals(category.id, fetchCategory.id)
        Assertions.assertEquals(1, fetchCategory.children.size)
        Assertions.assertEquals(child.id, fetchCategory.children[0].id)
    }

    @Test
    fun createWithChildTest() {
        val category = repository.saveAndFlush(CategoryDescription(children = listOf(CategoryDescription())))
        Assertions.assertNotNull(category)
        Assertions.assertNotNull(category.id)

        Assertions.assertEquals(1, category.children.size)
    }

    @Test
    fun findRootCategoriesTest() {
        repository.saveAndFlush(CategoryDescription())
        Assertions.assertEquals(1, repository.findAllRoot().size)
    }

    @Test
    fun createWithLocalizationTest() {
        val category = repository.saveAndFlush(CategoryDescription(localization = constructLocalization("en" to "Test")))

        Assertions.assertNotNull(category)
        Assertions.assertNotNull(category.id)

        val fetchCategory = repository.findByIdWithLocalization(category.id!!).orElse(null);
        Assertions.assertNotNull(fetchCategory)
        Assertions.assertNotNull(fetchCategory.id)

        Assertions.assertEquals(1, fetchCategory.localization.size)
        Assertions.assertEquals("Test", fetchCategory.localization["en"]?.name)
    }

    @Test
    fun updateLocalizationByObjectTest() {
        val category = repository.saveAndFlush(CategoryDescription())

        Assertions.assertNotNull(category)
        Assertions.assertNotNull(category.id)
        Assertions.assertEquals(0, category.localization.size)

        localizationRepository.save(CategoryLocalizedName(category, "en", "Test"))

        val fetchCategory = repository.findByIdWithLocalization(category.id!!).orElse(null);
        Assertions.assertNotNull(fetchCategory)
        Assertions.assertNotNull(fetchCategory.id)

        Assertions.assertEquals(1, fetchCategory.localization.size)
        Assertions.assertEquals("Test", fetchCategory.localization["en"]?.name)
    }

    @Test
    fun updateLocalizationByIdTest() {
        val category = repository.saveAndFlush(CategoryDescription())

        Assertions.assertNotNull(category)
        Assertions.assertNotNull(category.id)
        Assertions.assertEquals(0, category.localization.size)

        localizationRepository.save(CategoryLocalizedName(category.id, "en", "Test"))

        val fetchCategory = repository.findByIdWithLocalization(category.id!!).orElse(null);
        Assertions.assertNotNull(fetchCategory)
        Assertions.assertNotNull(fetchCategory.id)

        Assertions.assertEquals(1, fetchCategory.localization.size)
        Assertions.assertEquals("Test", fetchCategory.localization["en"]?.name)
    }

}