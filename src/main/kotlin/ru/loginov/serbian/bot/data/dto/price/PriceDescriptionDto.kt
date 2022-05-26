package ru.loginov.serbian.bot.data.dto.price

import ru.loginov.serbian.bot.data.dto.category.CategoryDto
import ru.loginov.serbian.bot.data.dto.product.ProductDescriptionDto
import ru.loginov.serbian.bot.data.dto.shop.ShopDescriptionDto
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
        uniqueConstraints = [
            UniqueConstraint(columnNames = ["shop_id, product_id"]),
            UniqueConstraint(columnNames = ["shop_id, category_id"])
        ]
)
class PriceDescriptionDto {

    @Id
    @GeneratedValue
    var id: Int? = null

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    var category: CategoryDto? = null

    @Column(name = "category_id")
    var categoryId: Int? = null

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    var product: ProductDescriptionDto? = null

    @Column(name = "product_id")
    var productId: Int? = null

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "shop_id", insertable = false, updatable = false)
    var shop: ShopDescriptionDto? = null

    @Column(name = "shop_id", nullable = false)
    var shopId: Int? = null

    var minPrice: Float? = null

    var maxPrice: Float? = null

    @Column(nullable = false)
    var createdDateTime: LocalDateTime? = null

    @Column(nullable = false)
    var lastUpdatedDateTime: LocalDateTime? = null
}