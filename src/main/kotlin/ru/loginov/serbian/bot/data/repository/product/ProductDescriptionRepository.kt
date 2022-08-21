package ru.loginov.serbian.bot.data.repository.product

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.product.ProductDescription
import java.util.Optional

@Repository
interface ProductDescriptionRepository : JpaRepository<ProductDescription, Int> {

    fun findAllByCategoryId(categoryId: Int): List<ProductDescription>

    @Query("SELECT dao FROM ProductDescription dao LEFT JOIN FETCH dao.localization WHERE dao.categoryId = :categoryId")
    fun findAllByCategoryIdWithLocalization(categoryId: Int): List<ProductDescription>

    @Query("SELECT dao FROM ProductDescription dao LEFT JOIN FETCH dao.localization")
    fun findAllWithLocalization(): List<ProductDescription>

    @Query("SELECT dao FROM ProductDescription dao LEFT JOIN FETCH dao.localization WHERE dao.id = :id")
    fun findByIdWithLocalization(id: Int): Optional<ProductDescription>

}