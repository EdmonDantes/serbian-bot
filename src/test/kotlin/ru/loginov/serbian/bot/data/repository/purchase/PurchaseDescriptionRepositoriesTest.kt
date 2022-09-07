package ru.loginov.serbian.bot.data.repository.purchase

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.loginov.serbian.bot.data.dto.category.CategoryDescription
import ru.loginov.serbian.bot.data.dto.place.CountryDescription
import ru.loginov.serbian.bot.data.dto.product.ProductDescription
import ru.loginov.serbian.bot.data.dto.purchase.PurchaseDescription
import ru.loginov.serbian.bot.data.dto.purchase.PurchaseTrustLevel
import ru.loginov.serbian.bot.data.dto.shop.ShopDescription
import ru.loginov.serbian.bot.data.dto.user.UserDescription
import ru.loginov.serbian.bot.data.repository.category.CategoryDescriptionRepository
import ru.loginov.serbian.bot.data.repository.place.CountryDescriptionRepository
import ru.loginov.serbian.bot.data.repository.product.ProductDescriptionRepository
import ru.loginov.serbian.bot.data.repository.shop.ShopDescriptionRepository
import ru.loginov.serbian.bot.data.repository.user.UserDescriptionRepository
import java.time.LocalDateTime
import java.util.stream.IntStream
import kotlin.properties.Delegates
import kotlin.streams.toList

@SpringBootTest(
        classes = [
            PurchaseDescriptionRepository::class,
            CategoryDescriptionRepository::class,
            ProductDescriptionRepository::class,
            ShopDescriptionRepository::class,
            CountryDescriptionRepository::class,
            UserDescriptionRepository::class
        ]
)
@EnableJpaRepositories(
        basePackages = [
            "ru.loginov.serbian.bot.data.repository.purchase",
            "ru.loginov.serbian.bot.data.repository.category",
            "ru.loginov.serbian.bot.data.repository.product",
            "ru.loginov.serbian.bot.data.repository.shop",
            "ru.loginov.serbian.bot.data.repository.place",
            "ru.loginov.serbian.bot.data.repository.user"
        ]
)
@EnableAutoConfiguration
@EntityScan(basePackages = ["ru.loginov.serbian.bot.data.*"])
class PurchaseDescriptionRepositoriesTest {

    @Autowired
    private lateinit var repository: PurchaseDescriptionRepository

    @Autowired
    private lateinit var categoryRepository: CategoryDescriptionRepository

    @Autowired
    private lateinit var productRepository: ProductDescriptionRepository

    @Autowired
    private lateinit var shopRepository: ShopDescriptionRepository

    @Autowired
    private lateinit var countryRepository: CountryDescriptionRepository

    @Autowired
    private lateinit var userRepository: UserDescriptionRepository

    private var createdCountryId: Int by Delegates.notNull()
    private var createdShopId: Int by Delegates.notNull()
    private var createdProductId: Int by Delegates.notNull()
    private var createdCategoryId: Int by Delegates.notNull()
    private var createdUserId: Int by Delegates.notNull()

    @BeforeEach
    fun beforeTest() {
        repository.deleteAll()
        repository.flush()
        productRepository.deleteAll()
        productRepository.flush()
        categoryRepository.deleteAll()
        categoryRepository.flush()
        shopRepository.deleteAll()
        shopRepository.flush()
        countryRepository.deleteAll()
        countryRepository.flush()

        createdCountryId = countryRepository.saveAndFlush(CountryDescription(name = "test")).id
                ?: error("Can not create country description")
        createdShopId = shopRepository.saveAndFlush(
                ShopDescription(
                        "Test shop",
                        createdCountryId,
                        "Test address",
                        0.0,
                        0.0
                )
        ).id ?: error("Can not create shop description")
        createdCategoryId = categoryRepository.saveAndFlush(CategoryDescription()).id
                ?: error("Can not create category description")
        createdProductId = productRepository.saveAndFlush(ProductDescription(categoryId = createdCategoryId)).id
                ?: error("Can not create product description")
        createdUserId = userRepository.saveAndFlush(
                UserDescription(
                        language = "none",
                        permissionGroup = "none"
                )
        ).id ?: error("Can not create user description")
    }

    @Test
    fun simpleTestWithCategory() {
        val purchase = repository.saveAndFlush(
                PurchaseDescription(
                        categoryId = createdCategoryId,
                        shopId = createdShopId,
                        userId = createdUserId,
                        purchaseTrustLevel = PurchaseTrustLevel.TRUSTED,
                        price = 200
                )
        )

        Assertions.assertNotNull(purchase)
        Assertions.assertNotNull(purchase.id)
    }

    @Test
    fun simpleTestWithProduct() {
        val purchase = repository.saveAndFlush(
                PurchaseDescription(
                        productId = createdProductId,
                        shopId = createdShopId,
                        userId = createdUserId,
                        purchaseTrustLevel = PurchaseTrustLevel.TRUSTED,
                        price = 200
                )
        )

        Assertions.assertNotNull(purchase)
        Assertions.assertNotNull(purchase.id)
    }


    @Test
    fun testFindCategoryAndShopWithoutPaging() {

        val secondCategory = categoryRepository.saveAndFlush(CategoryDescription())

        Assertions.assertNotNull(secondCategory)
        Assertions.assertNotNull(secondCategory.id)

        val purchases = IntStream.range(0, 1000).mapToObj {
            PurchaseDescription(
                    categoryId = if (it % 2 == 0) createdCategoryId else secondCategory.id,
                    shopId = createdShopId,
                    userId = createdUserId,
                    purchaseTrustLevel = PurchaseTrustLevel.TRUSTED,
                    price = it + 10
            )
        }.toList()

        repository.saveAllAndFlush(purchases)

        Assertions.assertEquals(1000, repository.count())

        val page = repository.findAllByCategoryIdAndShopIdAndCreatedDateTimeAfter(
                createdCategoryId,
                createdShopId,
                LocalDateTime.now().minusMonths(1)
        )

        Assertions.assertEquals(500, page.totalElements)
        Assertions.assertEquals(500, page.numberOfElements)
    }

    @Test
    fun testFindCategoryAndShopWithPaging() {

        val secondCategory = categoryRepository.saveAndFlush(CategoryDescription())

        Assertions.assertNotNull(secondCategory)
        Assertions.assertNotNull(secondCategory.id)

        val purchases = IntStream.range(0, 1000).mapToObj {
            PurchaseDescription(
                    categoryId = if (it % 2 == 0) createdCategoryId else secondCategory.id,
                    shopId = createdShopId,
                    userId = createdUserId,
                    purchaseTrustLevel = PurchaseTrustLevel.TRUSTED,
                    price = it + 10
            )
        }.toList()

        repository.saveAllAndFlush(purchases)

        Assertions.assertEquals(1000, repository.count())

        val page = repository.findAllByCategoryIdAndShopIdAndCreatedDateTimeAfter(
                createdCategoryId,
                createdShopId,
                LocalDateTime.now().minusMonths(1),
                Pageable.ofSize(100)
        )

        Assertions.assertEquals(500, page.totalElements)
        Assertions.assertEquals(100, page.numberOfElements)
    }


    @Test
    fun testFindProductAndShopWithoutPaging() {
        val secondProduct = productRepository.saveAndFlush(ProductDescription(categoryId = createdCategoryId))

        Assertions.assertNotNull(secondProduct)
        Assertions.assertNotNull(secondProduct.id)

        val purchases = IntStream.range(0, 1000).mapToObj {
            PurchaseDescription(
                    productId = if (it % 2 == 0) createdProductId else secondProduct.id,
                    shopId = createdShopId,
                    userId = createdUserId,
                    purchaseTrustLevel = PurchaseTrustLevel.TRUSTED,
                    price = it + 10
            )
        }.toList()

        repository.saveAllAndFlush(purchases)

        Assertions.assertEquals(1000, repository.count())

        val page = repository.findAllByProductIdAndShopIdAndCreatedDateTimeAfter(
                createdProductId,
                createdShopId,
                LocalDateTime.now().minusMonths(1)
        )

        Assertions.assertEquals(500, page.totalElements)
        Assertions.assertEquals(500, page.numberOfElements)
    }

    @Test
    fun testFindProductAndShopWithPaging() {
        val secondProduct = productRepository.saveAndFlush(ProductDescription(categoryId = createdCategoryId))

        Assertions.assertNotNull(secondProduct)
        Assertions.assertNotNull(secondProduct.id)

        val purchases = IntStream.range(0, 1000).mapToObj {
            PurchaseDescription(
                    productId = if (it % 2 == 0) createdProductId else secondProduct.id,
                    shopId = createdShopId,
                    userId = createdUserId,
                    purchaseTrustLevel = PurchaseTrustLevel.TRUSTED,
                    price = it + 10
            )
        }.toList()

        repository.saveAllAndFlush(purchases)

        Assertions.assertEquals(1000, repository.count())

        val page = repository.findAllByProductIdAndShopIdAndCreatedDateTimeAfter(
                createdProductId,
                createdShopId,
                LocalDateTime.now().minusMonths(1),
                Pageable.ofSize(100)
        )

        Assertions.assertEquals(500, page.totalElements)
        Assertions.assertEquals(100, page.numberOfElements)
    }

}