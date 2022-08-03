package ru.loginov.serbian.bot.data.repository.search

import org.hibernate.search.engine.search.query.SearchResult
import org.hibernate.search.mapper.orm.Search
import org.springframework.beans.factory.annotation.Autowired
import java.util.function.Function
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional


@Transactional
abstract class AbstractSearchRepository<E> : SearchRepository<E> {

    @Autowired
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    protected abstract val generalPropertyName: String

    override fun findAllByGeneralProperty(obj: String): List<E> =
            createSearchQuery(listOf(generalPropertyName), obj).hits() as List<E>

    override fun findAllBy(propertiesToSearch: List<String>, value: String): List<E> =
            createSearchQuery(propertiesToSearch, value).hits() as List<E>

    private fun createSearchQuery(propertiesToSearch: List<String>, value: String): SearchResult<*> =
            Search.session(entityManager).search(entityClass).where( Function {
                it.match().let { match ->
                    when {
                        propertiesToSearch.size == 1 -> {
                            match.field(propertiesToSearch[0])
                                    .matching(value)
                                    .fuzzy()
                        }
                        propertiesToSearch.size > 1 -> {
                            match.fields(*propertiesToSearch.toTypedArray())
                                    .matching(value)
                        }
                        else -> error("Can not create search for empty properties")
                    }
                }
            })
                    .fetch(20)
}