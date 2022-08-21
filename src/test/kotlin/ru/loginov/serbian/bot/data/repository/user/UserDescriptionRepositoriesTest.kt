package ru.loginov.serbian.bot.data.repository.user

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.findByIdOrNull
import ru.loginov.serbian.bot.data.dto.user.UserAdditionalData
import ru.loginov.serbian.bot.data.dto.user.UserDescription
import ru.loginov.serbian.bot.data.repository.assertNotNullAfter
import java.util.stream.Collectors
import java.util.stream.IntStream

@SpringBootTest(
        classes = [
            UserDescriptionRepository::class,
            UserAdditionalDataRepository::class
        ]
)
@EnableJpaRepositories
@EnableAutoConfiguration
@EntityScan(basePackages = ["ru.loginov.serbian.bot.data.*"])
class UserDescriptionRepositoriesTest {

    @Autowired
    private lateinit var repository: UserDescriptionRepository

    @Autowired
    private lateinit var additionalDataRepository: UserAdditionalDataRepository

    @BeforeEach
    fun beforeTest() {
        additionalDataRepository.deleteAll()
        repository.deleteAll()
    }

    @Test
    fun simpleTest() {
        repository.assertNotNullAfter(UserDescription(language = "en", permissionGroup = "default")) {
            saveAndFlush(it)
        }
    }

    @Test
    fun updateLanguageTest() {
        val user = repository.assertNotNullAfter(UserDescription(language = "en", permissionGroup = "default")) {
            saveAndFlush(it)
        }

        repository.updateLanguage(user.id!!, "en2")

        repository.assertNotNullAfter(UserDescription(language = "en2", permissionGroup = "default")) {
            repository.findByIdOrNull(user.id!!)
        }
    }

    @Test
    fun updatePermissionGroupTest() {
        val user = repository.assertNotNullAfter(UserDescription(language = "en", permissionGroup = "default")) {
            saveAndFlush(it)
        }

        repository.updatePermissionGroup(user.id!!, "not_default")

        repository.assertNotNullAfter(UserDescription(language = "en", permissionGroup = "not_default")) {
            repository.findByIdOrNull(user.id!!)
        }
    }

    @Test
    fun replacePermissionGroupTest() {
        val users = repository.saveAllAndFlush(
                IntStream
                        .range(0, 10)
                        .mapToObj {
                            UserDescription(
                                    language = "$it",
                                    permissionGroup = if (it % 2 == 0) "default" else "not_default"
                            )
                        }.collect(Collectors.toList())
        )

        users.sortBy { it.language?.toIntOrNull() ?: 0 }

        Assertions.assertEquals(10, users.size)
        users.forEachIndexed { index, it ->
            Assertions.assertNotNull(it)
            Assertions.assertNotNull(it.id)
            Assertions.assertEquals("$index", it.language)
            Assertions.assertEquals(if (index % 2 == 0) "default" else "not_default", it.permissionGroup)
        }

        repository.replacePermissionGroup("not_default", "new_group")

        val fetchUsers = repository.findAll()
        fetchUsers.sortBy { it.language?.toIntOrNull() ?: 0 }

        Assertions.assertEquals(10, fetchUsers.size)
        fetchUsers.forEachIndexed { index, it ->
            Assertions.assertNotNull(it)
            Assertions.assertNotNull(it.id)
            Assertions.assertEquals("$index", it.language)
            Assertions.assertEquals(if (index % 2 == 0) "default" else "new_group", it.permissionGroup)
        }
    }

    @Test
    fun addAdditionalDataTest() {
        val user = repository.assertNotNullAfter(UserDescription(language = "en", permissionGroup = "default")) {
            saveAndFlush(it)
        }

        additionalDataRepository.assertNotNullAfter(UserAdditionalData(user.id, "test", "test")) {
            saveAndFlush(it)
        }

        val data = additionalDataRepository.assertNotNullAfter(
                expected = UserAdditionalData(user.id, "test", "test"),
                input = null,
                ignoreProperties = listOf("createdDateTime", "lastUpdateDateTime", "user")
        ) {
            findByUserIdAndKey(user.id!!, "test").orElse(null)
        }

        Assertions.assertTrue(data.createdDateTime == data.lastUpdateDateTime)
    }

    @Test
    fun updateAdditionalDataTest() {

        val user = repository.assertNotNullAfter(UserDescription(language = "en", permissionGroup = "default")) {
            saveAndFlush(it)
        }

        val data = additionalDataRepository.assertNotNullAfter(
                obj = UserAdditionalData(user.id, "test", "test"),
                ignoreProperties = listOf("createdDateTime", "lastUpdateDateTime", "user")
        ) {
            saveAndFlush(it)
            findByUserIdAndKey(user.id!!, "test").orElse(null)
        }

        Assertions.assertTrue(data.createdDateTime == data.lastUpdateDateTime)

        val fetchData = additionalDataRepository.assertNotNullAfter(
                obj = UserAdditionalData(user.id, "test", "new_value"),
                ignoreProperties = listOf("createdDateTime", "lastUpdateDateTime", "user")
        ) {
            saveAndFlush(it)
            findByUserIdAndKey(user.id!!, "test").orElse(null)
        }

        Assertions.assertTrue(fetchData.lastUpdateDateTime.isAfter(fetchData.createdDateTime))
    }

    // TODO: add test for update some users, for update some keys, for remove some keys

    @Test
    fun removeAdditionalDataTest() {

        val user = repository.assertNotNullAfter(UserDescription(language = "en", permissionGroup = "default")) {
            saveAndFlush(it)
        }

        val data = additionalDataRepository.assertNotNullAfter(
                obj = UserAdditionalData(user.id, "test", "test"),
                ignoreProperties = listOf("createdDateTime", "lastUpdateDateTime", "user")
        ) {
            saveAndFlush(it)
            findByUserIdAndKey(user.id!!, "test").orElse(null)
        }

        Assertions.assertTrue(data.createdDateTime == data.lastUpdateDateTime)

        additionalDataRepository.removeData(user.id!!, "test")

        Assertions.assertNull(additionalDataRepository.findByUserIdAndKey(user.id!!, "test").orElse(null))
    }

}