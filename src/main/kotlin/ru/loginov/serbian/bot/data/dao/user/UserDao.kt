package ru.loginov.serbian.bot.data.dao.user

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UserDao {

    @Id
    var id: Long? = null

    var directChatId: Long? = null
}