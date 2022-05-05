package ru.loginov.serbian.bot.data.manager.permission.tree

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PermissionTreeTest {

//    private val tree = PermissionTree()
//
//    @Test
//    fun testAddSimplePermission() {
//        tree.addPermission(SIMPLE_PERMISSION)
//        assertTrue(tree.havePermission(SIMPLE_PERMISSION))
//        assertFalse(tree.havePermission(SIMPLE_PERMISSION_2))
//        assertFalse(tree.havePermission(LONG_SIMPLE_PERMISSION))
//    }
//
//    @Test
//    fun testAddAllPermissionFromGroup() {
//        tree.addPermission(ALL_PERMISSION_GROUP)
//        assertTrue(tree.havePermission(SIMPLE_PERMISSION))
//        assertTrue(tree.havePermission(SIMPLE_PERMISSION_2))
//        assertTrue(tree.havePermission(LONG_SIMPLE_PERMISSION))
//    }

    @Test
    fun testDeleteSimplePermission() {
//        tree.addPermission(ALL_PERMISSION_GROUP)
//        tree.deletePermission(SIMPLE_PERMISSION)
//
//        assertFalse(tree.havePermission(SIMPLE_PERMISSION))
//        assertTrue(tree.havePermission(SIMPLE_PERMISSION_2))
//        assertFalse(tree.havePermission(LONG_SIMPLE_PERMISSION))
//        assertTrue(tree.havePermission(LONG_SIMPLE_PERMISSION_2))
    }

    @Test
    fun testToString() {
//        tree.addPermission(SIMPLE_PERMISSION)
//        tree.addPermission(SIMPLE_PERMISSION_2)
//        tree.addPermission(LONG_SIMPLE_PERMISSION)
//        tree.deletePermission(LONG_SIMPLE_PERMISSION_2)
//
//        println(tree.toString())
    }

    companion object {
        private const val SIMPLE_PERMISSION = "test.permission"
        private const val SIMPLE_PERMISSION_2 = "test.second"
        private const val LONG_SIMPLE_PERMISSION = "test.permission.long"
        private const val LONG_SIMPLE_PERMISSION_2 = "test.second.long"
        private const val ALL_PERMISSION_GROUP = "test.*"
    }

}