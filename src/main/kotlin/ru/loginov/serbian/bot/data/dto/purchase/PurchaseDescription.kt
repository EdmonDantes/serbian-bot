package ru.loginov.serbian.bot.data.dto.purchase

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
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(indexes = [Index(name = "dateTimeIndex", columnList = "created")])
class PurchaseDescription {

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

    @Column(name = "price", nullable = false)
    var price: Float? = null

    @Column(name = "created", nullable = false)
    var createdDateTime: LocalDateTime = LocalDateTime.now()
}