package ru.loginov.serbian.bot.data.repository.category

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dao.category.CategoryDao

@Repository
interface CategoryDaoRepository : JpaRepository<CategoryDao, Int> {

    @Query("SELECT dao FROM CategoryDao dao JOIN FETCH dao.localization")
    fun findAllWithLocalization() : List<CategoryDao>

}