package ru.loginov.serbian.bot.data.manager.permission

import io.github.edmondantes.simple.permissions.manager.PermissionManager
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.loginov.serbian.bot.configuration.ExecutorsConfiguration
import ru.loginov.serbian.bot.configuration.InMemoryJdbcConfiguration

@SpringBootTest(classes = [InMemoryJdbcConfiguration::class, ExecutorsConfiguration::class])
@ComponentScan("ru.loginov.data.manager.permission")
@EnableJpaRepositories
class PermissionManagerTest {

    @Autowired
    private lateinit var permissionManager: PermissionManager

    @Test
    fun testAddSimplePermission() {
        val groupName = "testAddSimplePermission"
        assertTrue(permissionManager.createGroup(groupName))
        assertTrue(permissionManager.grant(groupName, SIMPLE_PERMISSION))
        val tree = permissionManager.group(groupName)!!
        assertTrue(tree.checkPermission(SIMPLE_PERMISSION))
        assertFalse(tree.checkPermission(SIMPLE_PERMISSION_2))
        assertFalse(tree.checkPermission(LONG_SIMPLE_PERMISSION))
    }

    @Test
    fun testAddAllPermissionFromGroup() {
        val groupName = "testAddAllPermissionFromGroup"
        assertTrue(permissionManager.createGroup(groupName))
        assertTrue(permissionManager.grant(groupName, ALL_PERMISSION_GROUP))
        val tree = permissionManager.group(groupName)!!
        assertTrue(tree.checkPermission(SIMPLE_PERMISSION))
        assertTrue(tree.checkPermission(SIMPLE_PERMISSION_2))
        assertTrue(tree.checkPermission(LONG_SIMPLE_PERMISSION))
    }

    @Test
    fun testDeleteSimplePermission() {
        val groupName = "testDeleteSimplePermission"
        assertTrue(permissionManager.createGroup(groupName))
        assertTrue(permissionManager.grant(groupName, ALL_PERMISSION_GROUP))
        assertTrue(permissionManager.deprive(groupName, SIMPLE_PERMISSION))
        val tree = permissionManager.group(groupName)!!

        assertFalse(tree.checkPermission(SIMPLE_PERMISSION))
        assertTrue(tree.checkPermission(SIMPLE_PERMISSION_2))
        assertFalse(tree.checkPermission(LONG_SIMPLE_PERMISSION))
        assertTrue(tree.checkPermission(LONG_SIMPLE_PERMISSION_2))
    }

    @Test
    fun testOverrideEnabledProperties() {
        val groupName = "testOverrideEnabledProperties"
        assertTrue(permissionManager.createGroup(groupName))
        assertTrue(permissionManager.grant(groupName, ALL_PERMISSION_GROUP))
        assertTrue(permissionManager.deprive(groupName, SIMPLE_PERMISSION))
        assertTrue(permissionManager.grant(groupName, ALL_PERMISSION_GROUP))

        val tree = permissionManager.group(groupName)!!

        assertTrue(tree.checkPermission(SIMPLE_PERMISSION))
        assertTrue(tree.checkPermission(SIMPLE_PERMISSION_2))
        assertTrue(tree.checkPermission(LONG_SIMPLE_PERMISSION))
        assertTrue(tree.checkPermission(LONG_SIMPLE_PERMISSION_2))
    }

    companion object {
        private const val SIMPLE_PERMISSION = "test.permission"
        private const val SIMPLE_PERMISSION_2 = "test.second"
        private const val LONG_SIMPLE_PERMISSION = "test.permission.long"
        private const val LONG_SIMPLE_PERMISSION_2 = "test.second.long"
        private const val ALL_PERMISSION_GROUP = "test.*"
    }

}