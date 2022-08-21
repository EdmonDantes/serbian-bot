package ru.loginov.serbian.bot.data.dto.purchase

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import ru.loginov.serbian.bot.data.dto.WithId
import ru.loginov.serbian.bot.data.dto.category.CategoryDescription
import ru.loginov.serbian.bot.data.dto.product.ProductDescription
import ru.loginov.serbian.bot.data.dto.shop.ShopDescription
import ru.loginov.serbian.bot.data.dto.user.UserDescription
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(indexes = [Index(name = "dateTimeIndex", columnList = "created_date_time desc, trust_level asc, id")])
class PurchaseDescription : WithId {

    constructor()
    constructor(
            id: Int? = null,
            categoryId: Int? = null,
            productId: Int? = null,
            shopId: Int? = null,
            userId: Int? = null,
            purchaseTrustLevel: PurchaseTrustLevel? = null,
            price: Number? = null
    ) {
        this.purchaseTrustLevel = purchaseTrustLevel
        this.price = preparePrice(price)
        this.categoryId = categoryId
        this.productId = productId
        this.shopId = shopId
        this.userId = userId
        this.id = id
    }

    @Id
    @GeneratedValue
    override var id: Int? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    var category: CategoryDescription? = null

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "category_id")
    var categoryId: Int? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    var product: ProductDescription? = null

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "product_id")
    var productId: Int? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "shop_id", insertable = false, updatable = false)
    var shop: ShopDescription? = null

    @Column(name = "shop_id", nullable = false)
    var shopId: Int? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    var user: UserDescription? = null

    @Column(name = "user_id", nullable = false)
    var userId: Int? = null

    @Embedded
    @JsonIgnore
    @AttributeOverrides(
            value = [AttributeOverride(
                    name = "_level",
                    column = Column(name = "trust_level", nullable = false)
            )]
    )
    var purchaseTrustLevel: PurchaseTrustLevel? = null

    @Column(name = "price", nullable = false)
    var price: Long? = null

    @Column(name = "created_date_time", nullable = false)
    var createdDateTime: LocalDateTime = LocalDateTime.now()


    private fun preparePrice(price: Number?): Long? = when (price) {
        is BigDecimal -> {
            price.multiply(BIG_DECIMAL_HUNDRED).toLong()
        }
        is Float, is Double -> {
            price.toLong() * 100 + (price.toDouble().mod(1.0) * 10.0).toLong()
        }
        else -> {
            price?.toLong()
        }
    }

    companion object {
        private val BIG_DECIMAL_HUNDRED = BigDecimal.valueOf(100)
    }
}