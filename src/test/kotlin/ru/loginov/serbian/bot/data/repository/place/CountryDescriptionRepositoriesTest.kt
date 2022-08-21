package ru.loginov.serbian.bot.data.repository.place

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.loginov.serbian.bot.data.dto.localization.constructLocalization
import ru.loginov.serbian.bot.data.dto.place.CountryDescription
import ru.loginov.serbian.bot.data.dto.place.CountryLocalizedName

@SpringBootTest(classes = [CountryDescriptionRepository::class, CountryLocalizedNameRepository::class])
@EnableJpaRepositories
@EnableAutoConfiguration
@EntityScan(basePackages = ["ru.loginov.serbian.bot.data.*"])
class CountryDescriptionRepositoriesTest {

    @Autowired
    private lateinit var repository: CountryDescriptionRepository

    @Autowired
    private lateinit var localizationRepository: CountryLocalizedNameRepository

    @BeforeEach
    fun beforeTest() {
        repository.deleteAll()
        localizationRepository.deleteAll()
    }

    @Test
    fun simpleTest() {
        val country = repository.saveAndFlush(CountryDescription(name = "en"))

        Assertions.assertNotNull(country)
        Assertions.assertNotNull(country.id)
        Assertions.assertEquals("en", country.name)
    }

    @Test
    fun createWithLocalizationTest() {
        val country = repository.saveAndFlush(
                CountryDescription(
                        name = "en",
                        localization = constructLocalization("en" to "Test")
                )
        )

        Assertions.assertNotNull(country)
        Assertions.assertNotNull(country.id)
        Assertions.assertEquals("en", country.name)

        val fetchCountry = repository.findByIdWithLocalization(country.id!!).orElse(null)

        Assertions.assertNotNull(fetchCountry)
        Assertions.assertNotNull(fetchCountry.id)
        Assertions.assertEquals("en", fetchCountry.name)
        Assertions.assertEquals(1, fetchCountry.localization.size)
        Assertions.assertEquals("Test", fetchCountry.localization["en"]?.name)
    }

    @Test
    fun updateLocalizationTest() {
        val country = repository.saveAndFlush(
                CountryDescription(
                        name = "en",
                        localization = constructLocalization("en" to "Test")
                )
        )

        Assertions.assertNotNull(country)
        Assertions.assertNotNull(country.id)
        Assertions.assertEquals("en", country.name)

        val fetchCountry = repository.findByIdWithLocalization(country.id!!).orElse(null)

        Assertions.assertNotNull(fetchCountry)
        Assertions.assertNotNull(fetchCountry.id)
        Assertions.assertEquals("en", fetchCountry.name)
        Assertions.assertEquals(1, fetchCountry.localization.size)
        Assertions.assertEquals("Test", fetchCountry.localization["en"]?.name)

        localizationRepository.saveAndFlush(CountryLocalizedName(fetchCountry, "en", "Test2"))

        val fetchCountry2 = repository.findByIdWithLocalization(country.id!!).orElse(null)


        Assertions.assertNotNull(fetchCountry2)
        Assertions.assertNotNull(fetchCountry2.id)
        Assertions.assertEquals("en", fetchCountry2.name)
        Assertions.assertEquals(1, fetchCountry2.localization.size)
        Assertions.assertEquals("Test2", fetchCountry2.localization["en"]?.name)
    }

}