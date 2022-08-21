package ru.loginov.serbian.bot.data.repository.purchase

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.SpringBootTest
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
import kotlin.properties.Delegates

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
        categoryRepository.deleteAll()
        productRepository.deleteAll()
        shopRepository.deleteAll()
        countryRepository.deleteAll()

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
    fun simpleTest() {
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

}