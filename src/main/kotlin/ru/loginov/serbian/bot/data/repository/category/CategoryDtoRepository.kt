package ru.loginov.serbian.bot.data.repository.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.category.CategoryDto
import java.util.Optional

@Repository
interface CategoryDtoRepository : JpaRepository<CategoryDto, Int> {

    @Query("SELECT dao FROM CategoryDto dao JOIN FETCH dao.localization")
    fun findAllWithLocalization(): List<CategoryDto>

    @Query("SELECT dao FROM CategoryDto dao JOIN FETCH dao.localization WHERE dao.parent IS NULL")
    fun findAllRootWithLocalization(): List<CategoryDto>

    @Query("SELECT dao FROM CategoryDto dao LEFT JOIN FETCH dao.localization WHERE dao.id = :id")
    fun findByIdWithLocalization(@Param("id") id: Int): Optional<CategoryDto>

    @Query("SELECT dao FROM CategoryDto dao LEFT JOIN FETCH dao.subCategories WHERE dao.id = :id")
    fun findByIdWithSubCategories(@Param("id") id: Int): Optional<CategoryDto>

    @Query("SELECT dao FROM CategoryDto dao LEFT JOIN FETCH dao.subCategories JOIN FETCH dao.localization WHERE dao.id = 1")
    fun findByIdWithAllFields(@Param("id") id: Int): Optional<CategoryDto>

}