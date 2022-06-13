package ru.loginov.simple.permissions.storage.impl

import ru.loginov.simple.permissions.storage.PermissionStorage

class ConstantPermissionStorage(permissions: List<String>) : PermissionStorage {
    override val permissions: Collection<String> = HashSet(permissions)
    override fun hasPermission(permission: String): Boolean = permissions.contains(permission)
}