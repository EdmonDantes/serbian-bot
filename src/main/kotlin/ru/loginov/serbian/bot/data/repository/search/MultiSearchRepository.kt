package ru.loginov.serbian.bot.data.repository.search

import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
@Transactional
interface MultiSearchRepository {

    fun find(map: Map<Class<*>, String>, value: String): List<Any>

}