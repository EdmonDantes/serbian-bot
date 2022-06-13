package ru.loginov.simple.permissions.spring.data.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.loginov.simple.permissions.spring.data.entity.PermissionGroupEntity

@Repository
interface PermissionGroupRepository : JpaRepository<PermissionGroupEntity, String>