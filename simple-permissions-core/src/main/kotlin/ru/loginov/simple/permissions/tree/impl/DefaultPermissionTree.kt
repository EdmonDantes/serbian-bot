package ru.loginov.simple.permissions.tree.impl

import ru.loginov.simple.permissions.entity.PermissionNode
import ru.loginov.simple.permissions.storage.Storage
import ru.loginov.simple.permissions.tree.NodeMutations
import ru.loginov.simple.permissions.tree.PermissionTree
import ru.loginov.simple.permissions.util.PermissionHelper.ALL_PERMISSION_STRING
import ru.loginov.simple.permissions.util.PermissionHelper.splitPermissionToPartsOrThrow
import java.util.LinkedList

/**
 * Class for checking permissions.
 * Allows saving, updating and checking permission like `a.b.c`
 */
class DefaultPermissionTree(
        nodeStorage: Storage<Int, PermissionNode>,
        rootNode: PermissionNode
) : PermissionTree {

    private val root: DefaultPermissionTreeNode

    init {
        val rootBuilder = DefaultPermissionTreeNode.Builder()
        fillBuilder(rootBuilder, nodeStorage, rootNode)
        this.root = rootBuilder.build()
    }

    override fun addPermission(permission: String): NodeMutations? {
        val parts = splitPermissionToPartsOrThrow(permission)
        return createNodesBy(parts, false)
    }

    override fun deletePermission(permission: String): NodeMutations? {
        val parts = splitPermissionToPartsOrThrow(permission)
        return createNodesBy(parts, true)
    }

    override fun checkPermission(permission: String): Boolean {
        val parts = splitPermissionToPartsOrThrow(permission)
        val (index, node) = findLastExistingNode(parts)
        return (node.isAllPermissionNode || index >= parts.size) && !node.isExcluded
    }

    //TODO: Rewrite to optimize search permission (search only unique parts)
    override fun checkAllPermission(permissions: List<String>): Boolean = permissions.all { checkPermission(it) }

    private fun fillBuilder(
            rootBuilder: DefaultPermissionTreeNode.Builder,
            nodeStorage: Storage<Int, PermissionNode>,
            rootNode: PermissionNode
    ) {
        val childQueue = LinkedList<Pair<DefaultPermissionTreeNode.Builder, Int>>()
        rootBuilder.entity = rootNode

        rootNode.childrenIds?.forEach {
            childQueue.addLast(rootBuilder to it)
        }

        while (childQueue.isNotEmpty()) {
            val (node, childId) = childQueue.pop()
            val child = nodeStorage.findById(childId)
            if (child?.value != null) {
                val childBuilder = DefaultPermissionTreeNode.Builder()
                childBuilder.entity = child
                node.children[child.value!!] = childBuilder
                child.childrenIds?.forEach {
                    childQueue.addLast(childBuilder to it)
                }
            }
        }
    }

    private fun createNodesBy(
            parts: List<String>,
            isExcluded: Boolean
    ): NodeMutations {
        var (index, currentNode) = findLastExistingNode(parts)

        val forDelete = ArrayList<Int>()

        val startNode: DefaultPermissionTreeNode.Builder
        var newNodeBuilder: DefaultPermissionTreeNode.Builder

        if (currentNode.isAllPermissionNode) {
            if (currentNode.isExcluded == isExcluded) {
                val forDelete = currentNode.parent?.children?.filter { it.key != "*" }?.mapNotNull { it.value.entity.id }
                return if (forDelete == null) {
                    NodeMutations.EMPTY
                } else {
                    NodeMutations(null, forDelete)
                }
            }

            startNode = currentNode.parent?.toBuilder()
                    ?: error(
                            "Can not find parent for current node with value '${currentNode.entity.value}' " +
                                    "and id '${currentNode.entity.id}'"
                    )
            newNodeBuilder = DefaultPermissionTreeNode.Builder()
            val value = parts[index - 1]
            startNode.children[value] = newNodeBuilder
            newNodeBuilder.entity = PermissionNode(
                    value = value,
                    excluded = isExcluded,
                    parentId = startNode.entity?.parentId
            )
        } else {
            startNode = currentNode.toBuilder()
            newNodeBuilder = startNode
        }

        while (index < parts.size) {
            val value = parts[index++]

            val nextNode = DefaultPermissionTreeNode.Builder()
            nextNode.entity = PermissionNode(null, value, true, null, null)
            if (value == ALL_PERMISSION_STRING) {
                val prevValue = newNodeBuilder.children.values.find { it.entity?.value == "*" }
                val childrenIds = newNodeBuilder.children.values.mapNotNull { it.entity?.id }

                if (prevValue != null) {
                    nextNode.entity = prevValue.entity
                    forDelete.addAll(childrenIds.filter { it != prevValue.entity?.id })
                } else {
                    forDelete.addAll(childrenIds)
                }

                newNodeBuilder.children = mutableMapOf("*" to nextNode)
            } else {
                val prevValue = newNodeBuilder.children.put(value, nextNode)
                if (prevValue != null) {
                    nextNode.entity = prevValue.entity
                    if (prevValue.entity?.excluded != true) {
                        prevValue.entity?.excluded = true
                    }
                }
            }
            newNodeBuilder = nextNode
        }

        newNodeBuilder.entity?.excluded = isExcluded

        return NodeMutations(startNode.build(), forDelete)
    }

    private fun findLastExistingNode(
            parts: List<String>,
            startNode: DefaultPermissionTreeNode = root
    ): Pair<Int, DefaultPermissionTreeNode> {
        var index = 0
        var currentNode: DefaultPermissionTreeNode = startNode

        while (index < parts.size) {
            val part = parts[index]

            val nextNode = currentNode.tryToGetNextNode(part)
            if (nextNode == null) {
                currentNode = currentNode.tryToGetAllPermissionNode() ?: break
                index++
                break
            }

            currentNode = nextNode
            index++
        }

        return index to currentNode
    }
}
