package ru.loginov.simple.permissions.tree.builder

import ru.loginov.simple.permissions.entity.PermissionNode
import ru.loginov.simple.permissions.storage.Storage

class PermissionNodeBuilder(private val permissionNodeStorage: Storage<Int, PermissionNode>) {
    private val node: PermissionNode = PermissionNode(counterId++, excluded = true)
    private val children = ArrayList<Int>()

    fun add(value: String, excluded: Boolean = false, func: PermissionNodeBuilder.() -> Unit = {}): Int {
        val node = entityNode(permissionNodeStorage) {
            this.node.parentId = node.id
            this.node.value = value
            this.node.excluded = excluded
            func(this)
        }

        children.add(node.id!!)
        return node.id!!
    }

    fun build(): PermissionNode = node.also {
        it.childrenIds = children
        permissionNodeStorage.save(it)
    }

    companion object {
        private var counterId = 1
    }
}

fun entityNode(
        permissionNodeStorage: Storage<Int, PermissionNode>,
        func: PermissionNodeBuilder.() -> Unit
): PermissionNode =
        PermissionNodeBuilder(permissionNodeStorage).apply(func).build()