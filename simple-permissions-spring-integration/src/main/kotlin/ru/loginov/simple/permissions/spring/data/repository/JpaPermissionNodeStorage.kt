package ru.loginov.simple.permissions.spring.data.repository

import io.github.edmondantes.simple.permissions.data.entity.PermissionNode
import io.github.edmondantes.simple.permissions.data.storage.PermissionNodeStorage
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import ru.loginov.simple.permissions.spring.data.entity.PermissionNodeEntity

@Component
@Qualifier("permissionNodeStorage")
class JpaPermissionNodeStorage(
        private val repo: PermissionNodeRepository
) : PermissionNodeStorage {


    override fun save(
            value: String?,
            excluded: Boolean,
            parentId: Int?,
            childrenBuilder: PermissionNodeStorage.(PermissionNode) -> List<PermissionNode>
    ): PermissionNode {
        val node = repo.save(PermissionNodeEntity(null, value, excluded, parentId, emptyList()))
        node.children = childrenBuilder(node).map { if (it is PermissionNodeEntity) it else PermissionNodeEntity(it) }
        return repo.save(node)
    }

    override fun save(value: String?, excluded: Boolean, parentId: Int?, childrenIds: List<Int>): PermissionNode {
        return repo.save(
                PermissionNodeEntity(null, value, excluded, parentId, childrenIds.map { PermissionNodeEntity(it) })
        )
    }

    override fun findById(id: Int): PermissionNode? = repo.findWithChildrenById(id).orElse(null)

    override fun update(
            id: Int,
            value: String?,
            excluded: Boolean?,
            parentId: Int?,
            childrenBuilder: PermissionNodeStorage.(PermissionNode) -> List<PermissionNode>?
    ): PermissionNode {
        val node = repo.save(PermissionNodeEntity(id, value, excluded, parentId))
        return childrenBuilder(node)?.let {
            node.children = it.map { if (it is PermissionNodeEntity) it else PermissionNodeEntity(it) }
            repo.save(node)
        } ?: node
    }

    override fun update(
            id: Int, value: String?, excluded: Boolean?, parentId: Int?, childrenIds: List<Int>?
    ): PermissionNode {
        return repo.save(PermissionNodeEntity(id,
                value,
                excluded,
                parentId,
                childrenIds?.map { PermissionNodeEntity(it) } ?: emptyList()))
    }


    override fun findAll(): List<PermissionNode> = repo.findAllWithChildren()

    override fun deleteById(id: Int) {
        repo.deleteById(id)
    }

    override fun deleteAllByIds(ids: List<Int>) {
        repo.deleteAllById(ids)
    }
}