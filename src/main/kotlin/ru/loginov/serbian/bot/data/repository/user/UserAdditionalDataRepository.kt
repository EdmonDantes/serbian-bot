package ru.loginov.serbian.bot.data.repository.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.loginov.serbian.bot.data.dto.user.UserAdditionalData
import java.util.Optional

@Repository
interface UserAdditionalDataRepository : JpaRepository<UserAdditionalData, Int> {

    @Query("SELECT dto FROM UserAdditionalData dto WHERE dto.id.userId = :userId AND dto.id.key = :key AND dto.value IS NOT NULL")
    fun findByUserIdAndKey(@Param("userId") userId: Int, @Param("key") key: String): Optional<UserAdditionalData>

    @Query("SELECT dto FROM UserAdditionalData dto WHERE dto.id.userId = :userId AND dto.id.key IN :keys AND dto.value IS NOT NULL")
    fun findAllByUserIdAndKeyIn(
            @Param("userId") userId: Int,
            @Param("keys") keys: List<String>
    ): List<UserAdditionalData>

    @Query("SELECT dto FROM UserAdditionalData dto WHERE dto.id.userId = :userId AND dto.value IS NOT NULL")
    fun findAllByUserId(@Param("userId") userId: Int): List<UserAdditionalData>

    @Query("SELECT dto FROM UserAdditionalData dto WHERE dto.id.key = :key AND dto.value = :value")
    fun findAllByKeyAndValue(@Param("key") key: String, @Param("value") value: String): List<UserAdditionalData>

    @Modifying
    @Transactional
    @Query("UPDATE UserAdditionalData dto SET dto.value = NULL, dto.lastUpdateDateTime = current_timestamp WHERE dto.id.userId = :userId AND dto.id.key = :key")
    fun removeData(@Param("userId") userId: Int, @Param("key") key: String)

    @Modifying
    @Transactional
    @Query("UPDATE UserAdditionalData dto SET dto.value = NULL, dto.lastUpdateDateTime = current_timestamp WHERE dto.id.userId IN :userIds AND dto.id.key = :key")
    fun removeData(@Param("userIds") userIds: List<Int>, @Param("key") key: String)
}