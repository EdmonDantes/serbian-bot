package ru.loginov.serbian.bot.data.repository.product

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.product.ProductDescriptionDto
import java.util.Optional

@Repository
interface ProductDescriptionDtoRepository : JpaRepository<ProductDescriptionDto, Int> {

    fun findAllByCategoryId(categoryId: Int): List<ProductDescriptionDto>

    @Query("SELECT dao FROM ProductDescriptionDto dao LEFT JOIN FETCH dao.localization WHERE dao.categoryId = :categoryId")
    fun findAllByCategoryIdWithLocalization(categoryId: Int): List<ProductDescriptionDto>

    @Query("SELECT dao FROM ProductDescriptionDto dao LEFT JOIN FETCH dao.localization")
    fun findAllWithLocalization(): List<ProductDescriptionDto>

    @Query("SELECT dao FROM ProductDescriptionDto dao LEFT JOIN FETCH dao.localization WHERE dao.id = :id")
    fun findByIdWithLocalization(id: Int): Optional<ProductDescriptionDto>

}