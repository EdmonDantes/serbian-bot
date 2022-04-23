package ru.loginov.serbian.bot.data.repository.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.category.SubCategoryDtoLocalization

@Repository
interface SubCategoryDtoLocalizationRepository : JpaRepository<SubCategoryDtoLocalization, Int> {}