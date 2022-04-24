package ru.loginov.serbian.bot.data.repository.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.user.UserDataDto

@Repository
interface UserDataDtoRepository : JpaRepository<UserDataDto, Long> {

    fun findByUserIdAndKey(userId: Long, key: String) : UserDataDto?
    fun findAllByUserIdAndKeyIn(userId: Long, keys: List<String>) : List<UserDataDto>
    fun findAllByUserId(userId: Long) : List<UserDataDto>

    fun removeAllByUserIdAndKey(userId: Long, key: String) : Long
    fun removeAllByUserIdAndKeyIn(userId: Long, keys: List<String>) : Long

}