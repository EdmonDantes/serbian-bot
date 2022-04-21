package ru.loginov.serbian.bot.data.repository.category

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dao.localization.LocalizedId
import ru.loginov.serbian.bot.data.dao.category.CategoryDaoLocalization

@Repository
interface CategoryDaoLocalizationRepository : CrudRepository<CategoryDaoLocalization, LocalizedId> {


    fun findAllByNameLike(name: String): List<CategoryDaoLocalization>

}