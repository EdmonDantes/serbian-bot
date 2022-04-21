package ru.loginov.serbian.bot.configuration

import org.hibernate.search.mapper.orm.Search
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.dao.category.CategoryDaoLocalization
import ru.loginov.serbian.bot.data.dao.category.SubCategoryDaoLocalization
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional


@Component
@Transactional
class InitSearchServices(@PersistenceContext val entityManager: EntityManager) : ApplicationListener<ApplicationReadyEvent> {
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        try {
            val searchSession = Search.session(entityManager)

            val indexer = searchSession.massIndexer(
                    CategoryDaoLocalization::class.java,
                    SubCategoryDaoLocalization::class.java
            ).threadsToLoadObjects(8)

            indexer.startAndWait()
        } catch (e: InterruptedException) {
            //TODO: Replace to slf4j
            println("An error occurred trying to build the search index: $e")
        }
    }


}