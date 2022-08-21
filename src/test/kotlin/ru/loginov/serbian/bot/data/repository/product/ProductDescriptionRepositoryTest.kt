package ru.loginov.serbian.bot.data.repository.product

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.loginov.serbian.bot.data.dto.category.CategoryDescription
import ru.loginov.serbian.bot.data.dto.localization.constructLocalization
import ru.loginov.serbian.bot.data.dto.product.ProductDescription
import ru.loginov.serbian.bot.data.dto.product.ProductLocalizedName
import ru.loginov.serbian.bot.data.repository.category.CategoryDescriptionRepository
import kotlin.properties.Delegates

@SpringBootTest(
        classes = [
            ProductDescriptionRepository::class,
            ProductLocalizedNameRepository::class,
            CategoryDescriptionRepository::class
        ]
)
@EnableJpaRepositories(
        basePackages = [
            "ru.loginov.serbian.bot.data.repository.category",
            "ru.loginov.serbian.bot.data.repository.product"
        ]
)
@EnableAutoConfiguration
@EntityScan(basePackages = ["ru.loginov.serbian.bot.data.*"])
class ProductDescriptionRepositoryTest {

    @Autowired
    private lateinit var repository: ProductDescriptionRepository

    @Autowired
    private lateinit var localizationRepository: ProductLocalizedNameRepository

    @Autowired
    private lateinit var categoryRepository: CategoryDescriptionRepository

    private var createdCategoryId by Delegates.notNull<Int>()

    @BeforeEach
    fun beforeTest() {
        repository.deleteAll()
        localizationRepository.deleteAll()
        categoryRepository.deleteAll()

        createdCategoryId = categoryRepository.saveAndFlush(CategoryDescription()).id
                ?: error("Can not create category")
    }

    @Test
    fun simpleTest() {
        val product = repository.saveAndFlush(ProductDescription(categoryId = createdCategoryId))

        Assertions.assertNotNull(product)
        Assertions.assertNotNull(product.id)

        Assertions.assertEquals(1, repository.count())
    }

    @Test
    fun findAllByCategoryTest() {
        val secondCategoryId = categoryRepository.saveAndFlush(CategoryDescription()).id
        Assertions.assertNotNull(secondCategoryId)

        val firstProduct = repository.saveAndFlush(ProductDescription(categoryId = createdCategoryId))
        val secondProduct = repository.saveAndFlush(ProductDescription(categoryId = secondCategoryId))
        val thirdProduct = repository.saveAndFlush(ProductDescription(categoryId = createdCategoryId))

        Assertions.assertNotNull(firstProduct)
        Assertions.assertNotNull(firstProduct.id)

        Assertions.assertNotNull(secondProduct)
        Assertions.assertNotNull(secondProduct.id)

        Assertions.assertNotNull(thirdProduct)
        Assertions.assertNotNull(thirdProduct.id)

        val firstAndThirdProducts = repository.findAllByCategoryId(createdCategoryId).mapNotNull { it.id }
        val onlySecondProduct = repository.findAllByCategoryId(secondCategoryId!!).mapNotNull { it.id }

        Assertions.assertEquals(2, firstAndThirdProducts.size)
        Assertions.assertEquals(1, onlySecondProduct.size)

        Assertions.assertTrue(firstAndThirdProducts.contains(firstProduct.id))
        Assertions.assertTrue(firstAndThirdProducts.contains(thirdProduct.id))
        Assertions.assertTrue(onlySecondProduct.contains(secondProduct.id))
    }

    @Test
    fun testLocalization() {
        val product = repository.saveAndFlush(
                ProductDescription(
                        categoryId = createdCategoryId,
                        localization = constructLocalization("en" to "Test")
                )
        )


        Assertions.assertNotNull(product)
        Assertions.assertNotNull(product.id)

        val productWithLocalization = repository.findByIdWithLocalization(product.id!!).orElse(null)

        Assertions.assertNotNull(productWithLocalization)
        Assertions.assertNotNull(productWithLocalization.id)
        Assertions.assertEquals(1, productWithLocalization.localization.size)
        Assertions.assertEquals("Test", productWithLocalization.localization["en"]?.name)
    }

    @Test
    fun updateLocalization() {
        val product = repository.saveAndFlush(ProductDescription(categoryId = createdCategoryId))

        Assertions.assertNotNull(product)
        Assertions.assertNotNull(product.id)

        localizationRepository.saveAndFlush(
                ProductLocalizedName(product.id!!, "en", "Test")
        )

        val productWithLocalization = repository.findByIdWithLocalization(product.id!!).orElse(null)

        Assertions.assertNotNull(productWithLocalization)
        Assertions.assertNotNull(productWithLocalization.id)
        Assertions.assertEquals(1, productWithLocalization.localization.size)
        Assertions.assertEquals("Test", productWithLocalization.localization["en"]?.name)
    }

}