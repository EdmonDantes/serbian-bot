package ru.loginov.serbian.bot.data.repository.place

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.place.CountryDescription
import java.util.Optional

@Repository
interface CountryDescriptionRepository : JpaRepository<CountryDescription, Int> {

    @Query("SELECT dao FROM CountryDescription dao LEFT JOIN FETCH dao.localization WHERE dao.id = :id ")
    fun findByIdWithLocalization(@Param("id") id: Int): Optional<CountryDescription>

}