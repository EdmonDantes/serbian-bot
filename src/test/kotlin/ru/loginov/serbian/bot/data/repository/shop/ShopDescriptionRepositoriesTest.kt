package ru.loginov.serbian.bot.data.repository.shop

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.loginov.serbian.bot.data.dto.place.CountryDescription
import ru.loginov.serbian.bot.data.dto.shop.ShopComment
import ru.loginov.serbian.bot.data.dto.shop.ShopDescription
import ru.loginov.serbian.bot.data.repository.place.CountryDescriptionRepository
import java.time.LocalDateTime
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.properties.Delegates

@SpringBootTest(
        classes = [
            ShopDescriptionRepository::class,
            ShopDescriptionCommentRepository::class,
            CountryDescriptionRepository::class
        ]
)
@EnableJpaRepositories(
        basePackages = [
            "ru.loginov.serbian.bot.data.repository.place",
            "ru.loginov.serbian.bot.data.repository.shop"
        ]
)
@EnableAutoConfiguration
@EntityScan(basePackages = ["ru.loginov.serbian.bot.data.*"])
class ShopDescriptionRepositoriesTest {

    @Autowired
    private lateinit var repository: ShopDescriptionRepository

    @Autowired
    private lateinit var commentRepository: ShopDescriptionCommentRepository

    @Autowired
    private lateinit var countryRepository: CountryDescriptionRepository

    private var createdCountryId: Int by Delegates.notNull()

    @BeforeEach
    fun beforeTest() {
        repository.deleteAll()
        commentRepository.deleteAll()
        countryRepository.deleteAll()

        createdCountryId = countryRepository.saveAndFlush(CountryDescription(name = "en")).id
                ?: error("Can not create new country description")
    }

    @Test
    fun simpleTest() {
        val description = repository.saveAndFlush(ShopDescription().apply {
            shopName = "Test shop"
            countryId = createdCountryId
            address = "Test address"
            latitude = 0.0
            longitude = 0.0
        })

        Assertions.assertNotNull(description)
        Assertions.assertNotNull(description.id)

        Assertions.assertEquals(1, repository.count())
    }

    @Test
    fun nullFieldTest() {
        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            repository.save(ShopDescription().apply {
                shopName = "Test shop"
                address = "Test address"
                latitude = 0.0
                longitude = 0.0
            })
        }
    }

    @Test
    fun commentTest() {
        val shop = repository.save(
                ShopDescription("Test shop", createdCountryId, "Test address", 0.0, 0.0)
        )

        Assertions.assertNotNull(shop)
        Assertions.assertNotNull(shop.id)

        commentRepository.saveAllAndFlush(
                IntStream.range(0, 40)
                        .mapToObj { ShopComment(shop.id!!, "test$it") }
                        .collect(Collectors.toList())
        )

        Assertions.assertEquals(40, commentRepository.count())

        var lastDateTime = LocalDateTime.now()

        for (i in 0 until 4) {
            val comments = commentRepository
                    .findTop10ByShopIdAndCreatedTimeBeforeOrderByCreatedTimeDesc(shop.id!!, lastDateTime)
            Assertions.assertEquals(10, comments.size)

            val iterator = comments.iterator()

            for (j in (40 - i * 10 - 1) downTo 40 - (i + 1) * 10) {
                Assertions.assertEquals("test$j", iterator.next().comment)
            }

            lastDateTime = comments.last().createdTime
        }
    }
}