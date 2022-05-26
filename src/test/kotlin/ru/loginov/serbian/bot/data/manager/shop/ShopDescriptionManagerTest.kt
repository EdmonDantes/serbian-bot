package ru.loginov.serbian.bot.data.manager.shop

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.loginov.serbian.bot.configuration.InMemoryJdbcConfiguration

@SpringBootTest(classes = [InMemoryJdbcConfiguration::class, ShopDescriptionManager::class])
@EnableJpaRepositories
class ShopDescriptionManagerTest {

    @Autowired
    private lateinit var shopDescriptionManager: ShopDescriptionManager

    @Test
    fun testCreatingByGoogleLink() {
        val shop = runBlocking {
            shopDescriptionManager.create("https://goo.gl/maps/eqaXJkLJk4rPzAEu5")
        }

        assertNotNull(shop)
        assertEquals("Google", shop!!.shopName)
        assertEquals("660 Lakeview Cir, Morganton, GA 30560, USA", shop.address)
    }

}