package ru.loginov.simple.permissions.spring.data.repository

import io.github.edmondantes.simple.permissions.data.entity.PermissionGroup
import io.github.edmondantes.simple.permissions.data.storage.PermissionGroupStorage
import io.github.edmondantes.simple.permissions.data.storage.PermissionNodeStorage
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import ru.loginov.simple.permissions.spring.data.entity.PermissionGroupEntity

@Component
@Qualifier("permissionGroupStorage")
class JpaPermissionGroupStorage(
        private val permissionNodeStorage: PermissionNodeStorage,
        private val repo: PermissionGroupRepository
) : PermissionGroupStorage {

    override fun save(name: String, rootNodeId: Int?): PermissionGroup {
        return repo.save(
                PermissionGroupEntity(
                        name,
                        rootNodeId ?: permissionNodeStorage.save(excluded = true).id
                )
        )
    }

    override fun findById(id: String): PermissionGroup? =
            repo.findById(id).orElse(null)

    override fun findAll(): List<PermissionGroup> =
            repo.findAll()

    override fun deleteById(id: String) {
        repo.deleteById(id)
    }

    override fun deleteAllByIds(ids: List<String>) {
        repo.deleteAllById(ids)
    }
}