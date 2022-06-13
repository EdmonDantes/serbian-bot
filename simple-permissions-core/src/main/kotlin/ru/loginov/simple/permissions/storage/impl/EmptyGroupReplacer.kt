package ru.loginov.simple.permissions.storage.impl

import ru.loginov.simple.permissions.storage.GroupReplacer

class EmptyGroupReplacer : GroupReplacer {
    override fun replace(old: String, new: String): Boolean = false
}