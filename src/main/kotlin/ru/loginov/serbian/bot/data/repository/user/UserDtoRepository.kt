package ru.loginov.serbian.bot.data.repository.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.user.UserDto

@Repository
interface UserDtoRepository : JpaRepository<UserDto, Long> {

    @Modifying
    @Query("UPDATE UserDto dto SET dto.permissionGroup = :newGroup WHERE dto.permissionGroup = :oldGroup")
    fun replacePermissionGroup(@Param("oldGroup") oldGroup: String, @Param("newGroup") newGroup: String?)

}