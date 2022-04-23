package ru.loginov.serbian.bot.configuration

import org.hibernate.search.mapper.orm.Search
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.dto.category.CategoryDtoLocalization
import ru.loginov.serbian.bot.data.dto.category.SubCategoryDtoLocalization
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
                    CategoryDtoLocalization::class.java,
                    SubCategoryDtoLocalization::class.java
            ).threadsToLoadObjects(8)

            indexer.startAndWait()
        } catch (e: InterruptedException) {
            //TODO: Replace to slf4j
            println("An error occurred trying to build the search index: $e")
        }
    }


}