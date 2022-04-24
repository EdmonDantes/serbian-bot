package ru.loginov.serbian.bot.data.dto.user

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UserDto {

    @Id
    var id: Long? = null

    /**
     * Direct chat id
     */
    var chatId: Long? = null

    /**
     * Language for user
     */
    var language: String? = null

    /**
     * Additional data for user (custom)
     * If you want to modify it, please use manager's methods
     */
    @Transient // Will be set in manager, because we can save many data for differnet commands
    var additionalData: Map<String, String> = emptyMap()
}