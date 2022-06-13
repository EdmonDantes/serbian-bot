package ru.loginov.simple.permissions.spring.data.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.loginov.simple.permissions.entity.PermissionNode
import ru.loginov.simple.permissions.spring.data.entity.PermissionNodeEntity
import ru.loginov.simple.permissions.storage.Storage

@Component
class JpaPermissionNodeStorage : Storage<Int, PermissionNode> {

    @Autowired
    private lateinit var repo: PermissionNodeRepository

    override fun save(obj: PermissionNode): PermissionNode? {
        return repo.save(PermissionNodeEntity(obj))
    }

    override fun saveAll(list: List<PermissionNode>): List<PermissionNode> =
            repo.saveAll(list.map { PermissionNodeEntity(it) })

    override fun findById(id: Int): PermissionNode? =
            repo.findWithChildrenById(id).orElse(null)


    override fun findAll(): List<PermissionNode> = repo.findAllWithChildren()

    override fun deleteById(id: Int) {
        repo.deleteById(id)
    }

    override fun deleteAllByIds(list: List<Int>) {
        repo.deleteAllById(list)
    }
}