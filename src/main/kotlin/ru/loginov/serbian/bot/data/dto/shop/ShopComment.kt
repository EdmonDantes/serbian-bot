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
@Table(indexes = [Index(name = "indexByTime", columnList = "created_time desc,shop_id asc,id")])
class ShopComment {

    constructor() {}
    constructor(id: Int) {
        this.id = id
    }

    constructor(shopId: Int, comment: String, locale: String? = null) {
        this.shopId = shopId
        this.locale = locale
        this.comment = comment
        this.createdTime = LocalDateTime.now()
    }


    @Id
    @GeneratedValue
    var id: Int? = null

    @ManyToOne
    @JoinColumn(name = "shop_id", insertable = false, updatable = false)
    var shop: ShopDescription? = null

    @Column(name = "shop_id", nullable = false)
    var shopId: Int? = null

    @Column(name = "locale")
    var locale: String? = null

    @Column(nullable = false)
    var comment: String? = null

    @Column(name = "created_time", nullable = false)
    var createdTime: LocalDateTime? = null

}