package ru.loginov.simple.permissions.storage.impl

import ru.loginov.simple.permissions.storage.Storage
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractInMemoryStorage<ID, T> : Storage<ID, T> {

    private val storage = ConcurrentHashMap<ID, T>()

    override fun save(obj: T): T? {
        val id = getIdFromObj(obj) ?: generateIdForObj(obj)
        storage[id] = obj
        return obj
    }

    override fun saveAll(list: List<T>): List<T> =
            list.mapNotNull { save(it) }

    override fun findById(id: ID): T? =
            storage[id]

    override fun findAll(): List<T> =
            storage.values.toList()

    override fun deleteById(id: ID) {
        storage.remove(id)
    }

    override fun deleteAllByIds(list: List<ID>) {
        list.forEach {
            deleteById(it)
        }
    }

    protected abstract fun getIdFromObj(obj: T): ID?
    protected abstract fun generateIdForObj(obj: T): ID
}