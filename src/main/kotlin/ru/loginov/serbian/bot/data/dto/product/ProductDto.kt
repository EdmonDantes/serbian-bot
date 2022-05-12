package ru.loginov.serbian.bot.data.dto.product

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import ru.loginov.serbian.bot.data.dto.category.CategoryDto
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapKey
import javax.persistence.OneToMany

@Entity
class ProductDto {

    @Id
    @GeneratedValue
    var id: Int? = null

    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    var category: CategoryDto? = null

    @Column(name = "category_id", nullable = false)
    var categoryId: Int? = null

    @MapKey(name = "localizedId.locale")
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    @OneToMany(mappedBy = "entity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var localization: MutableMap<String, ProductDtoLocalization> = HashMap()
}