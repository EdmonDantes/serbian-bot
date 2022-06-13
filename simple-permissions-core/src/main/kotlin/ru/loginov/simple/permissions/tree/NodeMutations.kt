package ru.loginov.simple.permissions.tree

/**
 * Describes what nodes should insertOrUpdate and what one should delete
 */
class NodeMutations(
        val forInsertOrUpdate: PermissionTreeNode? = null,
        val forDelete: List<Int> = emptyList()
) {
    companion object {
        val EMPTY: NodeMutations = NodeMutations()
    }
}