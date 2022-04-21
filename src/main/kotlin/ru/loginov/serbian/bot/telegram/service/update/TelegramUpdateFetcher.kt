package ru.loginov.serbian.bot.telegram.service.update

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.dao.telegram.UpdateSequence
import ru.loginov.serbian.bot.data.repository.telegram.UpdateSequenceRepository
import ru.loginov.serbian.bot.data.repository.telegram.UpdateSequenceRepository.Companion.DEFAULT_ID
import ru.loginov.serbian.bot.telegram.service.DefaultTelegramService
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import kotlin.math.max

@Service
class TelegramUpdateFetcher {

    @Autowired
    private lateinit var onUpdateHandlers: List<OnUpdateHandler>

    @Autowired
    private lateinit var updateSequenceRepository: UpdateSequenceRepository

    @Autowired
    private lateinit var telegramService: DefaultTelegramService

    @Autowired
    @Qualifier("smalltasks")
    private lateinit var executor: Executor

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
                telegramService.getUpdates(lastSeq)
            }.forEach { update ->
                newLastSeq = max(newLastSeq ?: Long.MIN_VALUE, update.id + 1)
                onUpdateHandlers.forEach { handler ->
                    executor.execute {
                        handler.onUpdate(update)
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
}