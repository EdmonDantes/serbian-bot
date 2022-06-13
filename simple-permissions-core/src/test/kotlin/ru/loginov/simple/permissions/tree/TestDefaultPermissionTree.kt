package ru.loginov.simple.permissions.tree

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.loginov.simple.permissions.exception.NotValidPermissionFormatException
import ru.loginov.simple.permissions.tree.builder.permissionTree

class TestDefaultPermissionTree {

    @Test
    fun testAddSimplePermissionToEmptyTree() {
        val tree = permissionTree()
        val mutations = tree.addPermission("a.b.c")

        assertNotNull(mutations)
        assertTrue(mutations!!.forDelete.isEmpty(), "Mutations for delete is not empty")
        assertNotNull(mutations.forInsertOrUpdate, "New node was not created")

        var node: PermissionTreeNode? = mutations.forInsertOrUpdate?.children?.get("a")
        assertNotNull(node, "Can not find node with value 'a'")
        node = node?.children?.get("b")
        assertNotNull(node, "Can not find node with value 'b'")
        node = node?.children?.get("c")
        assertNotNull(node, "Can not find node with value 'c'")
    }

    @Test
    fun testAddPermissionWithAllAndGetConflictInTree() {
        var forRemoveId: Int? = null
        val tree = permissionTree {
            add("a") {
                add("b") {
                    forRemoveId = add("d", true)
                }
                add("c")
            }
        }

        val mutations = tree.addPermission("a.b.*")
        assertNotNull(mutations)
        assertEquals(1, mutations!!.forDelete.size)
        assertEquals(forRemoveId, mutations.forDelete.first())

        assertNotNull(mutations.forInsertOrUpdate?.children?.get("*"), "Can not find node with value '*'")
    }

    @Test
    fun testAddSimplePermissionToTreeWithIgnoreAll() {
        val tree = permissionTree {
            add("a") {
                add("b") {
                    add("*", true)
                    add("f")
                }
                add("c")
            }
        }

        val mutations = tree.addPermission("a.b.d.e")
        assertNotNull(mutations)
        assertTrue(mutations!!.forDelete.isEmpty(), "Mutations for delete is not empty")
        assertNotNull(mutations.forInsertOrUpdate, "New node was not created")

        var node: PermissionTreeNode? = mutations.forInsertOrUpdate?.children?.get("d")
        assertNotNull(node, "Can not find node with value 'd'")
        node = node?.children?.get("e")
        assertNotNull(node, "Can not find node with value 'e'")
    }

    @Test
    fun testWrongPermissions() {
        val tree = permissionTree { }

        fun checkPermissionStr(permission: String) {
            assertThrows<NotValidPermissionFormatException>("Not valid permission format for '$permission'") {
                tree.addPermission(permission)
            }
        }

        checkPermissionStr("")
        checkPermissionStr("a.b.c.4")
        checkPermissionStr("6.g")
        checkPermissionStr("*.w")
        checkPermissionStr("ad5.*")
        checkPermissionStr("s.**")
        checkPermissionStr("abcd.*.as")

    }
}