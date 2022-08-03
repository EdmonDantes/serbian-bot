package ru.loginov.serbian.bot.data.dto.price

import ru.loginov.serbian.bot.data.dto.category.CategoryDescription
import ru.loginov.serbian.bot.data.dto.product.ProductDescriptionDto
import ru.loginov.serbian.bot.data.dto.shop.ShopDescription
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
        uniqueConstraints = [
            UniqueConstraint(columnNames = ["shop_id", "product_id"]),
            UniqueConstraint(columnNames = ["shop_id", "category_id"])
        ]
)
class PriceDescription {

    @Id
    @GeneratedValue
    var id: Int? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    var category: CategoryDescription? = null

    @Column(name = "category_id")
    var categoryId: Int? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    var product: ProductDescriptionDto? = null

    @Column(name = "product_id")
    var productId: Int? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id", insertable = false, updatable = false)
    var shop: ShopDescription? = null

    @Column(name = "shop_id", nullable = false)
    var shopId: Int? = null

    var minPrice: Float? = null

    var maxPrice: Float? = null

    @Column(name = "created_date_time", nullable = false)
    var createdDateTime: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_date_time", nullable = false)
    var lastUpdateDateTime: LocalDateTime = LocalDateTime.now()
}