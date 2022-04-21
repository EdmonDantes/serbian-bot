package ru.loginov.serbian.bot.data.repository.search

import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
@Transactional
interface SearchRepository<E> {
  fun findAllByGeneralProperty(obj: String) : List<E>
  fun findAllBy(propertiesToSearch: List<String>, value: String) : List<E>
}