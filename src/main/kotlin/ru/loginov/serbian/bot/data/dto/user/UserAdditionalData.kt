package ru.loginov.serbian.bot.data.dto.user

import java.time.LocalDateTime
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(
        indexes = [Index(name = "user_id_and_key", columnList = "create_date_time desc, user_id, data_key")]
)
class UserAdditionalData {

    constructor()

    constructor(userId: Int? = null, key: String? = null, value: String? = null) {
        this.id.userId = userId
        this.id.key = key
        this.value = value
    }

    @EmbeddedId
    @AttributeOverrides(
            AttributeOverride(name = "userId", column = Column(name = "user_id", nullable = false)),
            AttributeOverride(name = "key", column = Column(name = "data_key", nullable = false))
    )
    var id: UserAdditionalDataKey = UserAdditionalDataKey()

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    var user: UserDescription? = null

    @Column(name = "data_value")
    var value: String? = null

    @Column(name = "create_date_time", nullable = false, updatable = false)
    var createdDateTime: LocalDateTime = LocalDateTime.now()

    @Column(name = "last_update_date_time", nullable = false)
    var lastUpdateDateTime: LocalDateTime = this.createdDateTime

}