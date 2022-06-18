package ru.loginov.simple.permissions.spring

import ru.loginov.simple.permissions.storage.PermissionStorage

interface PermissionRegister : PermissionStorage {

    fun register(permission: String): Boolean

}