package ru.loginov.serbian.bot.data.dto.product

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import ru.loginov.serbian.bot.data.dto.localization.LocalizedName
import javax.persistence.Entity

@Entity
@Indexed
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
class ProductLocalizedName : LocalizedName<ProductDescription> {
    constructor() : super()
    constructor(entity: ProductDescription?, locale: String?, name: String?) : super(entity, locale, name)
    constructor(entityId: Int?, locale: String?, name: String?)
            : super(entityId?.let { ProductDescription(it) }, locale, name)
}