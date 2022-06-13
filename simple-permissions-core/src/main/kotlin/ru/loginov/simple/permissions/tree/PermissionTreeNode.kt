package ru.loginov.simple.permissions.tree

import ru.loginov.simple.permissions.entity.PermissionNode

interface PermissionTreeNode {

    val parent: PermissionTreeNode?
    val children: Map<String, PermissionTreeNode>
    val entity: PermissionNode

    val isAllPermissionNode: Boolean
    val isExcluded: Boolean

    fun tryToGetNextNode(node: String): PermissionTreeNode?
    fun tryToGetAllPermissionNode(): PermissionTreeNode?

    fun tryToGetValue(): String?

    @Throws(IllegalStateException::class)
    fun getValue(): String
}