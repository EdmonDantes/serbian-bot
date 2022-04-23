package ru.loginov.serbian.bot.data.dto.telegram

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UpdateSequence() {
    constructor(id: Long, seq: Long) : this() {
        this.id = id
        this.seq = seq
    }

    @Id
    var id: Long? = null

    @Column(nullable = false)
    var seq: Long? = null
}