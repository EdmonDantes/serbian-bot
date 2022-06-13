package ru.loginov.simple.permissions.manager

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.loginov.simple.permissions.manager.impl.DefaultPermissionManager
import ru.loginov.simple.permissions.storage.impl.ConstantPermissionStorage
import ru.loginov.simple.permissions.storage.impl.InMemoryPermissionGroupStorage
import ru.loginov.simple.permissions.storage.impl.InMemoryPermissionNodeStorage

class DefaultPermissionManagerTest {
    private val permissionStorage = ConstantPermissionStorage(listOf("a", "a.b", "a.b", "a.c", "a.b.d", "a.b.*", "a.*"))

    @Test
    fun simpleTest() {
        val manager = createManager()

        assertTrue(manager.createGroup("test"), "Can not create group with name 'test'")
        assertTrue(
                manager.addPermissionForGroup("test", "a.c"),
                "Can not add permission 'a.c' to group with name 'test'"
        )
        assertTrue(
                manager.addPermissionForGroup("test", "a.b.*"),
                "Can not add permission 'a.b.*' to group with name 'test'"
        )
        assertTrue(
                manager.deletePermissionForGroup("test", "a.b.d"),
                "Can not delete permission 'a.b.d' from group with name 'test'"
        )

        val owner1 = manager.getOwnerForGroup("test")
        assertNotNull(owner1, "Can not get owner for group with name 'test'")
        assertFalse(
                owner1!!.checkPermission("a.b.d"),
                "Permission 'a.b.d' wasn't deleted from group with name 'test'"
        )

        assertTrue(
                owner1.checkPermission("a.c"),
                "Permission 'a.c' wasn't added to group with name 'test'"
        )


        assertTrue(
                manager.addPermissionForGroup("test", "a.b.*"),
                "Can not add permission 'a.b.*' to group with name 'test'"
        )

        val owner2 = manager.getOwnerForGroup("test")
        assertNotNull(owner2, "Can not get owner for group with name 'test'")
        assertTrue(owner2!!.checkPermission("a.b.d"), "Permission 'a.b.*' wasn't added to group with name 'test'")
    }


    private fun createManager(): PermissionManager =
            DefaultPermissionManager(
                    InMemoryPermissionGroupStorage(),
                    InMemoryPermissionNodeStorage(),
                    permissionStorage
            )

}