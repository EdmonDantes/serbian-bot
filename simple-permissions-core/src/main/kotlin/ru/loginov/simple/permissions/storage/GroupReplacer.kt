package ru.loginov.simple.permissions.storage

interface GroupReplacer {
    fun replace(old: String, new: String): Boolean
}