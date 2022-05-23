package ru.loginov.serbian.bot.data.repository.category

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.category.CategoryDtoLocalization
import ru.loginov.serbian.bot.data.dto.localization.LocalizedId

@Repository
interface CategoryDtoLocalizationRepository : CrudRepository<CategoryDtoLocalization, LocalizedId>