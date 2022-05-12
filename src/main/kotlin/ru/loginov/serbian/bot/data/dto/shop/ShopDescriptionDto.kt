package ru.loginov.serbian.bot.data.dto.shop

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class ShopDescriptionDto {

    /**
     * Google map ID
     */
    @Id
    var id: String? = null

    @Column(nullable = false)
    var shopName: String? = null

    var googleMapLink: String? = null

    @Column(nullable = false)
    var address: String? = null

    var floor: Int? = null

    @OneToMany(mappedBy = "entity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<ShopDescriptionCommentDto>? = null
}