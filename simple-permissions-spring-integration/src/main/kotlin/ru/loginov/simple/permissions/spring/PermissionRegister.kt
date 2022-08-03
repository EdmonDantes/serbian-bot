package ru.loginov.simple.permissions.spring

import io.github.edmondantes.simple.permissions.data.storage.PermissionStorage

interface PermissionRegister : PermissionStorage {

    fun register(permission: String): Boolean

}