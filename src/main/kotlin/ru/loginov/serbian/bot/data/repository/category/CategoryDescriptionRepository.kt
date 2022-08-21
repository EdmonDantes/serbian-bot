package ru.loginov.serbian.bot.data.repository.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.category.CategoryDescription
import java.util.Optional

@Repository
interface CategoryDescriptionRepository : JpaRepository<CategoryDescription, Int> {

    @Query("SELECT dao FROM CategoryDescription dao JOIN FETCH dao.localization")
    fun findAllWithLocalization(): List<CategoryDescription>

    @Query("SELECT dao FROM CategoryDescription dao WHERE dao.parent IS NULL")
    fun findAllRoot(): List<CategoryDescription>

    @Query("SELECT dao FROM CategoryDescription dao JOIN FETCH dao.localization WHERE dao.parent IS NULL")
    fun findAllRootWithLocalization(): List<CategoryDescription>

    @Query("SELECT dao FROM CategoryDescription dao LEFT JOIN FETCH dao.localization WHERE dao.id = :id")
    fun findByIdWithLocalization(@Param("id") id: Int): Optional<CategoryDescription>

    @Query("SELECT dao FROM CategoryDescription dao LEFT JOIN FETCH dao.children WHERE dao.id = :id")
    fun findByIdWithSubCategories(@Param("id") id: Int): Optional<CategoryDescription>

    @Query("SELECT dao FROM CategoryDescription dao LEFT JOIN FETCH dao.children LEFT JOIN FETCH dao.localization WHERE dao.id = :id")
    fun findByIdWithAllFields(@Param("id") id: Int): Optional<CategoryDescription>

}