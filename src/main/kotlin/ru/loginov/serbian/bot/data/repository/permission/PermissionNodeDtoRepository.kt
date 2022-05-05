package ru.loginov.serbian.bot.data.repository.permission

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.loginov.serbian.bot.data.dto.permission.PermissionDto

interface PermissionNodeDtoRepository : JpaRepository<PermissionDto, Int>