package ru.loginov.serbian.bot.data.repository.search

import org.hibernate.search.mapper.orm.Search
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Transactional
class DefaultMultiSearchRepository : MultiSearchRepository {

    @Autowired
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun find(map: Map<Class<*>, String>, value: String): List<Any> {
        val classes = map.keys
        val properties = map.values.toTypedArray()

        return Search.session(entityManager).search(classes).where {
            it.match().fields(*properties).matching(value).fuzzy()
        }.fetch(20).hits() as List<Any>
    }
}