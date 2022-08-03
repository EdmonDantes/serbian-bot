package ru.loginov.serbian.bot.data.repository.category

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.category.CategoryLocalizedName
import ru.loginov.serbian.bot.data.dto.localization.LocalizedId

@Repository
interface CategoryDtoLocalizationRepository : CrudRepository<CategoryLocalizedName, LocalizedId>