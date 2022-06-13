package ru.loginov.simple.permissions.storage.impl

import ru.loginov.simple.permissions.entity.PermissionGroup

class InMemoryPermissionGroupStorage : AbstractInMemoryStorage<String, PermissionGroup>() {
    override fun getIdFromObj(obj: PermissionGroup): String? {
        return obj.name
    }

    override fun generateIdForObj(obj: PermissionGroup): String {
        error("Can not save permission group without name")
    }
}