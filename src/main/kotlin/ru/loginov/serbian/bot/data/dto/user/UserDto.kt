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
}