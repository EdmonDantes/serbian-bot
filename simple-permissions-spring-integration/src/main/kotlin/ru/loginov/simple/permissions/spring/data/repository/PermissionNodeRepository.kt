package ru.loginov.simple.permissions.spring.data.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.loginov.simple.permissions.spring.data.entity.PermissionNodeEntity
import java.util.Optional

@Repository
interface PermissionNodeRepository : JpaRepository<PermissionNodeEntity, Int> {

    @EntityGraph(value = "with_children")
    fun findWithChildrenById(id: Int): Optional<PermissionNodeEntity>

    @Query("SELECT dto FROM PermissionNodeEntity dto INNER JOIN dto.children")
    fun findAllWithChildren(): List<PermissionNodeEntity>

}