package ru.loginov.serbian.bot.data.repository.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dao.category.SubCategoryDao

@Repository
interface SubCategoryDaoRepository : JpaRepository<SubCategoryDao, Int> {}