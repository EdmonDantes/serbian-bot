package ru.loginov.serbian.bot.data.repository.permission

import org.springframework.data.jpa.repository.JpaRepository
import ru.loginov.serbian.bot.data.dto.permission.GroupPermissionDto

interface GroupPermissionDtoRepository : JpaRepository<GroupPermissionDto, String>