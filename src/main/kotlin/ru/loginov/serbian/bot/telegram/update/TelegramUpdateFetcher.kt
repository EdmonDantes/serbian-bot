package ru.loginov.serbian.bot.telegram.update

import kotlinx.coroutines.runBlocking
import org.hibernate.id.IdentifierGenerator
import org.hibernate.id.IncrementGenerator
import org.hibernate.id.enhanced.SequenceStyleGenerator
import org.hibernate.id.factory.IdentifierGeneratorFactory
import org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.dto.telegram.UpdateSequence
import ru.loginov.serbian.bot.data.repository.telegram.UpdateSequenceRepository
import ru.loginov.serbian.bot.data.repository.telegram.UpdateSequenceRepository.Companion.DEFAULT_ID
import ru.loginov.telegram.api.TelegramAPI
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.persistence.SequenceGenerator
import kotlin.math.max

@Service
class TelegramUpdateFetcher {

    @Autowired
    private lateinit var onUpdateHandlers: List<OnUpdateHandler>

    @Autowired
    private lateinit var updateSequenceRepository: UpdateSequenceRepository

    @Autowired
    private lateinit var telegramService: TelegramAPI

    @Autowired
    @Qualifier("small_tasks")
    private lateinit var executor: Executor

    @Value("\${bot.long.polling.timeout.sec:5}")
    private var longPollingTimeoutSec: Long = 5

    private val isContinue = AtomicBoolean(true)

    @PostConstruct
    fun postConstruct() {
        val lastSeq = updateSequenceRepository.findById(DEFAULT_ID).orElse(null)?.seq

        fetchUpdates(lastSeq)
    }

    private fun fetchUpdates(lastSeq: Long?) {
        executor.execute {
            var newLastSeq: Long? = lastSeq
            runBlocking {
                try {
                    telegramService.getUpdates {
                        offset = lastSeq
                        timeoutSec = longPollingTimeoutSec
                    }
                } catch (e: Exception) {
                    LOGGER.warn("Can not get updates for telegram bot", e)
                    emptyList()
                }
            }.forEach { update ->
                newLastSeq = max(newLastSeq ?: Long.MIN_VALUE, update.id + 1)
                onUpdateHandlers.forEach { handler ->
                    executor.execute {
                        try {
                            handler.onUpdate(update)
                        } catch (e: Exception) {
                            LOGGER.error("Can not execute 'onUpdate' for handler: ${handler.javaClass}", e)
                        }
                    }
                }
            }

            if (newLastSeq != lastSeq) {
                updateSequenceRepository.saveAndFlush(UpdateSequence(newLastSeq!!, DEFAULT_ID))
            }

            if (isContinue.get()) {
                fetchUpdates(newLastSeq)
            }
        }
    }


    @PreDestroy
    fun preDestroy() {
        isContinue.set(false)
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TelegramUpdateFetcher::class.java)
    }
}