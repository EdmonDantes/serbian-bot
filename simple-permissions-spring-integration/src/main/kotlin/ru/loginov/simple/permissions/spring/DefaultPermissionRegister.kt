package ru.loginov.simple.permissions.spring

import org.springframework.stereotype.Component
import ru.loginov.simple.permissions.permission.PermissionValidator
import ru.loginov.simple.permissions.storage.PermissionStorage

@Component
class DefaultPermissionRegister(private val permissionValidator: PermissionValidator) : PermissionRegister, PermissionStorage {
    override val permissions: MutableSet<String> = HashSet()

    override fun hasPermission(permission: String): Boolean = permissions.contains(permission)

    override fun register(permission: String): Boolean =
            if (permissionValidator.validate(permission)) {
                permissions.add(permission)
                true
            } else {
                false
            }

}