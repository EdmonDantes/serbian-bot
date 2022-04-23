package ru.loginov.serbian.bot.data.dto.shop

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
class InShopDescriptionComment {

    @Id
    @GeneratedValue
    var id: Int? = null

    @ManyToOne
    @JoinColumn(name = "entity_id", insertable = false, updatable = false)
    var entity: InShopDescription? = null

    @Column(name = "entity_id", nullable = false)
    var entityId: Int? = null

    @Column(nullable = false)
    var comment: String? = null

}