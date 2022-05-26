package ru.loginov.serbian.bot.data.repository.product

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.localization.LocalizedId
import ru.loginov.serbian.bot.data.dto.product.ProductDescriptionDtoLocalization

@Repository
interface ProductDescriptionDtoLocalizationRepository : JpaRepository<ProductDescriptionDtoLocalization, LocalizedId>