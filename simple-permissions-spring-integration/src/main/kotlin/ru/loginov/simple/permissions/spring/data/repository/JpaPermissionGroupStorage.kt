package ru.loginov.simple.permissions.spring.data.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.simple.permissions.entity.PermissionGroup
import ru.loginov.simple.permissions.spring.data.entity.PermissionGroupEntity
import ru.loginov.simple.permissions.storage.Storage

@Component
class JpaPermissionGroupStorage : Storage<String, PermissionGroup> {

    @Autowired
    private lateinit var repo: PermissionGroupRepository

    override fun save(obj: PermissionGroup): PermissionGroup? =
            repo.save(PermissionGroupEntity(obj))

    override fun saveAll(list: List<PermissionGroup>): List<PermissionGroup> =
            repo.saveAll(list.map { PermissionGroupEntity(it) })

    override fun findById(id: String): PermissionGroup? =
            repo.findById(id).orElse(null)

    override fun findAll(): List<PermissionGroup> =
            repo.findAll()

    override fun deleteById(id: String) {
        repo.deleteById(id)
    }

    override fun deleteAllByIds(list: List<String>) {
        repo.deleteAllById(list)
    }
}