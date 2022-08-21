package ru.loginov.serbian.bot.data.repository.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.loginov.serbian.bot.data.dto.user.UserDescription

@Repository
interface UserDescriptionRepository : JpaRepository<UserDescription, Int> {

    @Modifying
    @Transactional
    @Query("UPDATE UserDescription dto SET dto.language = :lang WHERE dto.id = :userId")
    fun updateLanguage(@Param("userId") userId: Int, @Param("lang") lang: String)

    @Modifying
    @Transactional
    @Query("UPDATE UserDescription dto SET dto.permissionGroup = :group WHERE dto.id = :userId")
    fun updatePermissionGroup(@Param("userId") userId: Int, @Param("group") group: String)

    @Modifying
    @Transactional
    @Query("UPDATE UserDescription dto SET dto.permissionGroup = :newGroup WHERE dto.permissionGroup = :oldGroup")
    fun replacePermissionGroup(@Param("oldGroup") oldGroup: String, @Param("newGroup") newGroup: String)

}