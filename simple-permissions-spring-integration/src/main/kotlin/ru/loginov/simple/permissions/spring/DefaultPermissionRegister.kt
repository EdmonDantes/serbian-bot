package ru.loginov.simple.permissions.spring

import io.github.edmondantes.simple.permissions.data.storage.PermissionStorage
import io.github.edmondantes.simple.permissions.validator.PermissionValidator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
@Qualifier("permissionStorage")
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