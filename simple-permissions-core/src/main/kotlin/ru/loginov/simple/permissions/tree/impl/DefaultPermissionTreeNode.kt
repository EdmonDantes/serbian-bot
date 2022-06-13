package ru.loginov.simple.permissions.tree.impl

import ru.loginov.simple.permissions.entity.PermissionNode
import ru.loginov.simple.permissions.tree.PermissionTreeNode
import ru.loginov.simple.permissions.util.PermissionHelper

/**
 * Node for [DefaultPermissionTree]
 */
class DefaultPermissionTreeNode : PermissionTreeNode {

    override val parent: DefaultPermissionTreeNode?
    override val children: Map<String, DefaultPermissionTreeNode>
    override val entity: PermissionNode
    override val isAllPermissionNode: Boolean
        get() = entity.value == PermissionHelper.ALL_PERMISSION_STRING
    override val isExcluded: Boolean
        get() = entity.excluded == true

    constructor(
            entity: PermissionNode,
            children: Map<String, DefaultPermissionTreeNode> = HashMap(),
            parent: DefaultPermissionTreeNode? = null
    ) {
        this.parent = parent
        this.children = children
        this.entity = entity
    }

    private constructor(
            parent: DefaultPermissionTreeNode?,
            children: Map<String, Builder>,
            entity: PermissionNode
    ) {
        this.parent = parent
        this.children = children.mapValues { it.value.also { it.parent = this }.build() }
        this.entity = entity
    }

    override fun tryToGetNextNode(part: String): DefaultPermissionTreeNode? = children[part]

    override fun tryToGetAllPermissionNode(): DefaultPermissionTreeNode? = children[PermissionHelper.ALL_PERMISSION_STRING]

    override fun tryToGetValue(): String? = entity.value

    override fun getValue(): String =
            entity.value ?: error("Can not get value for not permission node")

    fun toBuilder(): Builder = Builder().also {
        it.parent = parent
        it.children = children.mapValues { it.value.toBuilder() }.toMutableMap()
        it.entity = entity
    }

    class Builder {
        var parent: DefaultPermissionTreeNode? = null
        var children: MutableMap<String, Builder> = HashMap()
        var entity: PermissionNode? = null

        fun build(): DefaultPermissionTreeNode {
            return DefaultPermissionTreeNode(parent, children, entity ?: error("Entity can not be null"))
        }
    }
}
