package ru.loginov.serbian.bot.configuration

import org.hibernate.search.mapper.orm.Search
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.dto.category.CategoryDtoLocalization
import ru.loginov.serbian.bot.data.dto.product.ProductDescriptionDtoLocalization
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional


@Component
@Transactional
class SearchRepositoryConfiguration(@PersistenceContext val entityManager: EntityManager) : ApplicationListener<ApplicationReadyEvent> {
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        try {
            Search.session(entityManager)
                    .massIndexer(
                            CategoryDtoLocalization::class.java,
                            ProductDescriptionDtoLocalization::class.java
                    )
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