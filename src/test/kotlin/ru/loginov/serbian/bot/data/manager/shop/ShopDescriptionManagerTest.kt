package ru.loginov.serbian.bot.data.manager.shop

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.loginov.serbian.bot.configuration.InMemoryJdbcConfiguration
import ru.loginov.serbian.bot.data.dto.shop.ShopDescription


@SpringBootTest(classes = [InMemoryJdbcConfiguration::class, ShopDescriptionManager::class])
@EnableJpaRepositories
class ShopDescriptionManagerTest {

    @Autowired
    private lateinit var shopDescriptionManager: ShopDescriptionManager

    @Test
    fun testCreatingByGoogleLink() {
        if (System.getenv()["MAPS_API_KEY"].isNullOrEmpty()) {
            System.err.println("Test 'testCreatingByGoogleLink' in class '${this.javaClass}' will be ignored, because env var 'MAPS_API_KEY' did not set")
            return
        }

        val shop = runBlocking {
            shopDescriptionManager.create("https://goo.gl/maps/eqaXJkLJk4rPzAEu5")
        }

        assertNotNull(shop)
        assertEquals("Google", shop!!.shopName)
        assertEquals("660 Lakeview Cir, Morganton, GA 30560, USA", shop.address)
    }

    @Test
    fun testCreatingByNameAndAddress() {
        val shop = runBlocking {
            shopDescriptionManager.create("For test", "65 Lakeview, NewYork, USA")
        }

        assertNotNull(shop)
        assertEquals("For test", shop!!.shopName)
        assertEquals("65 Lakeview, NewYork, USA", shop.address)
    }

    @Test
    fun testNearFind() {
        for (i in 1..20) {
            val shop = runBlocking {
                shopDescriptionManager.create("$i", "$i", latitude = i.toDouble(), longitude = i.toDouble())
            }

            assertNotNull(shop, "Can not save shop with i=$i")
        }

        val nearest = runBlocking {
            shopDescriptionManager.findNearest(10.0, 10.0)
        }.sortedWith { a, b -> a.latitude!!.compareTo(b.latitude!!) }.map { it.shopName!!.toIntOrNull()!! }

        for (i in 6..15) {
            assertTrue(nearest.contains(i))
        }
    }

    @Test
    fun testExistsByIdPositive() {
        assertTrue(runBlocking { shopDescriptionManager.existsById(createShop("testExistsByIdPositive").id!!) })
    }

    @Test
    fun testExistsByIdNegative() {
        assertFalse(runBlocking { shopDescriptionManager.existsById(1) })
    }

    @Test
    fun testRemovePositive() {
        assertTrue(runBlocking { shopDescriptionManager.remove(createShop("testRemovePositive").id!!) })
    }

    @Test
    fun testRemoveNegative() {
        assertFalse(runBlocking { shopDescriptionManager.remove(1) })
    }

    @Test
    fun testAddCommentPositive() {
        assertTrue(runBlocking {
            shopDescriptionManager.addComment(
                    createShop("testAddCommentPositive").id!!,
                    "Test comment"
            )
        })
    }

    @Test
    fun testAddCommentNegative() {
        assertFalse(runBlocking { shopDescriptionManager.addComment(1, "Test comment") })
    }

    @Test
    fun testGetComments() {
        val shopId = createShop("testGetComments").id!!
        for (i in 1..100) {
            assertTrue(runBlocking { shopDescriptionManager.addComment(shopId, "$i") })
            Thread.sleep(5)
        }

        val comments = runBlocking { shopDescriptionManager.getComments(shopId) }
        val commentsNumber = comments.mapNotNull { it.comment?.toIntOrNull() }
        assertEquals(10, commentsNumber.size)
        for (i in 91..100) {
            assertTrue(commentsNumber.contains(i))
        }

        val newComments = runBlocking {
            shopDescriptionManager.getComments(
                    shopId,
                    comments.mapNotNull { it.createdTime }.minOrNull()
            )
        }
                .mapNotNull { it.comment?.toIntOrNull() }
        assertEquals(10, comments.size)
        for (i in 81..90) {
            assertTrue(newComments.contains(i))
        }
    }


    private fun createShop(methodName: String): ShopDescription {
        val shop = runBlocking {
            shopDescriptionManager.create("Test '$methodName''", "Test address")
        }

        assertNotNull(shop)
        assertNotNull(shop!!.id)
        return shop
    }


}