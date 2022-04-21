package ru.loginov.serbian.bot.data.dao.category

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.util.Locale
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MapKey
import javax.persistence.OneToMany

@Entity
class CategoryDao {

    @Id
    @GeneratedValue
    var id: Int? = null

    @MapKey(name = "localizedId.locale")
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    @OneToMany(mappedBy = "entity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var localization: MutableMap<String, CategoryDaoLocalization> = HashMap()

    fun putLocalization(locale: Locale, value: String) {
        val localeTag = locale.toLanguageTag()
        localization[localeTag] = CategoryDaoLocalization(this, localeTag, value)
    }

    fun putLocalization(locale: String, value: String) {
        localization[locale] = CategoryDaoLocalization(this, locale, value)
    }

}