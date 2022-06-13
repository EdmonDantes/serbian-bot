package ru.loginov.simple.permissions.storage

interface Storage<ID, T> {

    fun save(obj: T): T?
    fun saveAll(list: List<T>): List<T>

    fun findById(id: ID): T?
    fun findAll(): List<T>

    fun deleteById(id: ID)
    fun deleteAllByIds(list: List<ID>)

}