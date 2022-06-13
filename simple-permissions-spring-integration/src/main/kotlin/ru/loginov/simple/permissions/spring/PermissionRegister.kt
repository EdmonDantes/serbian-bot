package ru.loginov.simple.permissions.spring

interface PermissionRegister {

    fun register(permission: String): Boolean

}