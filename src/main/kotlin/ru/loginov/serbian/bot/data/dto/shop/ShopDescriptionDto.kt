package ru.loginov.serbian.bot.data.dto.shop

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
@Indexed
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
    @KeywordField
    var shopName: String? = null

    var googleMapLink: String? = null

    @Column(nullable = false)
    var address: String? = null

    var floor: Int? = null

    var latitude: Double? = null

    var longitude: Double? = null

    @OneToMany(mappedBy = "entity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<ShopDescriptionCommentDto>? = null
}