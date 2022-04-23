package ru.loginov.serbian.bot.data.repository.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.category.CategoryDto

@Repository
interface CategoryDtoRepository : JpaRepository<CategoryDto, Int> {

    @Query("SELECT dao FROM CategoryDto dao JOIN FETCH dao.localization")
    fun findAllWithLocalization() : List<CategoryDto>

}