package ru.loginov.serbian.bot.configuration

import org.hibernate.search.mapper.orm.Search
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import ru.loginov.serbian.bot.data.repository.search.SearchRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional


@Component
@Transactional
@EnableTransactionManagement
class SearchRepositoryConfiguration(
        @PersistenceContext val entityManager: EntityManager,
        private val repositories: List<SearchRepository<*>>
) : ApplicationListener<ApplicationReadyEvent> {
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        try {
            Search.session(entityManager)
                    .massIndexer(repositories.map { it.entityClass })
                    .threadsToLoadObjects(8)
                    .startAndWait()
            LOGGER.info("Successfully started search repositories")
        } catch (e: InterruptedException) {
            LOGGER.error("An error occurred trying to build the search index", e)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchRepositoryConfiguration::class.java)
    }
}