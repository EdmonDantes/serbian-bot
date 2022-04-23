package ru.loginov.serbian.bot.data.dto.category

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.util.Locale
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MapKey
import javax.persistence.OneToMany

@Entity
class CategoryDto {

    @Id
    @GeneratedValue
    var id: Int? = null

    @MapKey(name = "localizedId.locale")
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    @OneToMany(mappedBy = "entity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var localization: MutableMap<String, CategoryDtoLocalization> = HashMap()

    fun putLocalization(locale: Locale, value: String) {
        val localeTag = locale.toLanguageTag()
        localization[localeTag] = CategoryDtoLocalization(this, localeTag, value)
    }

    fun putLocalization(locale: String, value: String) {
        localization[locale] = CategoryDtoLocalization(this, locale, value)
    }

}