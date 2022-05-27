package ru.loginov.serbian.bot.data.dto.shop

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
//TODO: Add location for better search
//TODO: Split address for better search (address, city, country)
class ShopDescriptionDto {

    @Id
    @GeneratedValue
    var id: Int? = null

    // This column has to can contain multiple null rows
    @Column(nullable = true, unique = true)
    var googleMapId: String? = null

    @Column(nullable = false)
    var shopName: String? = null

    var googleMapLink: String? = null

    @Column(nullable = false)
    var address: String? = null

    var floor: Int? = null

    @OneToMany(mappedBy = "entity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<ShopDescriptionCommentDto>? = null
}