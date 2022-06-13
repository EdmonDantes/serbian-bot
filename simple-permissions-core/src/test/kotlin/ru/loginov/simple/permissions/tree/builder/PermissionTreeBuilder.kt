package ru.loginov.simple.permissions.tree.builder

import ru.loginov.simple.permissions.entity.PermissionNode
import ru.loginov.simple.permissions.storage.Storage
import ru.loginov.simple.permissions.storage.impl.InMemoryPermissionNodeStorage
import ru.loginov.simple.permissions.tree.impl.DefaultPermissionTree

fun permissionTree(
        permissionStorage: Storage<Int, PermissionNode> = InMemoryPermissionNodeStorage(),
        func: PermissionNodeBuilder.() -> Unit = {}
): DefaultPermissionTree =
        DefaultPermissionTree(permissionStorage, entityNode(permissionStorage, func))