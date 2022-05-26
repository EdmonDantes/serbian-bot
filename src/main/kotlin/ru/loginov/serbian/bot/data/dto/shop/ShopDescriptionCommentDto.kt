package ru.loginov.serbian.bot.data.dto.shop

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Table(indexes = [Index(name = "indexByTime", columnList = "entity_id asc,created_time desc,id")])
class ShopDescriptionCommentDto {

    @Id
    @GeneratedValue
    var id: Int? = null

    @ManyToOne
    @JoinColumn(name = "entity_id", insertable = false, updatable = false)
    var entity: ShopDescriptionDto? = null

    @Column(name = "entity_id", nullable = false)
    var entityId: Int? = null

    @Column(nullable = false)
    var comment: String? = null

    @Column(name = "created_time", nullable = false)
    var createdTime: LocalDateTime? = null

}