package ru.loginov.serbian.bot.data.dto.user

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "key"])])
class UserDataDto {

    @Id
    @GeneratedValue
    var id: Long? = null

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    var user: UserDto? = null

    @Column(name = "user_id", nullable = false)
    var userId: Long? = null

    @Column(nullable = false)
    var key: String? = null

    var value: String? = null

}