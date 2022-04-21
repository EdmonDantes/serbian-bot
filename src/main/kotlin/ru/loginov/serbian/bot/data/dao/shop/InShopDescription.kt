package ru.loginov.serbian.bot.data.dao.shop

import ru.loginov.serbian.bot.data.dao.category.CategoryDao
import ru.loginov.serbian.bot.data.dao.category.SubCategoryDao
import ru.loginov.serbian.bot.data.dao.product.ProductDao
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class InShopDescription {

    @Id
    @GeneratedValue
    var id: Int? = null

    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    var category: CategoryDao? = null

    @Column(name = "category_id", nullable = false)
    var categoryId: Int? = null

    @ManyToOne
    @JoinColumn(name = "sub_category_id", insertable = false, updatable = false)
    var subCategory: SubCategoryDao? = null

    @Column(name = "sub_category_id", nullable = false)
    var subCategoryId: Int? = null

    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    var product: ProductDao? = null

    @Column(name = "product_id", nullable = false)
    var productId: Int? = null

    var minimumCost: Float? = null

    var maximumCost: Float? = null

    var googleMapsLink: String? = null

    var address: String? = null

    @Column(nullable = false)
    var shopName: String? = null

    var floor: Int? = null

    @OneToMany(mappedBy = "entity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<InShopDescriptionComment>? = null
}