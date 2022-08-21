package ru.loginov.serbian.bot.data.dto.shop

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField
import ru.loginov.serbian.bot.data.dto.place.CountryDescription
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
@Indexed
//TODO: Add location for better search
//TODO: Split address for better search (address, city, country)
class ShopDescription {

    constructor() {}

    constructor(id: Int?) {
        this.id = id;
    }

    constructor(shopName: String, countryId: Int, address: String, latitude: Double, longitude: Double) {
        this.shopName = shopName
        this.countryId = countryId
        this.address = address
        this.latitude = latitude
        this.longitude = longitude
    }


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

    @JsonIgnore
    @ManyToOne(cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "country_id", insertable = false, updatable = false)
    var country: CountryDescription? = null

    @Column(name = "country_id", nullable = false)
    var countryId: Int? = null

    @Column(nullable = false)
    var address: String? = null

    var floor: Int? = null

    @Column(nullable = false)
    var latitude: Double? = null

    @Column(nullable = false)
    var longitude: Double? = null

    @OneToMany(mappedBy = "shop", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<ShopComment>? = null
}