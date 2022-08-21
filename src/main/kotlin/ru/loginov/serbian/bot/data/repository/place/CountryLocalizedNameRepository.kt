package ru.loginov.serbian.bot.data.repository.place

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.localization.LocalizedId
import ru.loginov.serbian.bot.data.dto.place.CountryLocalizedName

@Repository
interface CountryLocalizedNameRepository : JpaRepository<CountryLocalizedName, LocalizedId>