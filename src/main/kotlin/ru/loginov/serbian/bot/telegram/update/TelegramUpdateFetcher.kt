package ru.loginov.serbian.bot.telegram.update

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.dto.telegram.UpdateSequenceDto
import ru.loginov.serbian.bot.data.repository.telegram.UpdateSequenceRepository
import ru.loginov.serbian.bot.data.repository.telegram.UpdateSequenceRepository.Companion.DEFAULT_ID
import ru.loginov.telegram.api.TelegramAPI
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import kotlin.concurrent.thread
import kotlin.math.max

@Service
class TelegramUpdateFetcher(
        private val onUpdateHandlers: List<OnUpdateHandler>,
        private val updateSequenceRepository: UpdateSequenceRepository,
        private val telegram: TelegramAPI,
        private val coroutineScope: CoroutineScope,
        @Value("\${bot.telegram.update.timeout.sec:5}") private val longPollingTimeoutSec: Long = 5,
        @Value("\${bot.telegram.update.enable:true}") private val enabled: Boolean = true
) {
    private val fetcherThreadContext = Job()
    private val isContinue = AtomicBoolean(true)
    private val fetcherThread = thread(name = "Telegram Update Fetcher", start = false) {
        var lastSeq: Long? = updateSequenceRepository.findById(DEFAULT_ID).orElse(null)?.seq
        var i = 0
        while (isContinue.get()) {
            while (i < 1000) {
                lastSeq = fetchUpdates(lastSeq)
                i++
            }
        }
    }

    @PostConstruct
    fun postConstruct() {
        if (enabled) {
            fetcherThread.start()
        }
    }

    private fun fetchUpdates(lastSeq: Long?): Long? {
        val updates =
                try {
                    runBlocking(fetcherThreadContext) {
                        telegram.getUpdates {
                            offset = lastSeq
                            timeoutSec = longPollingTimeoutSec
                        }
                    }
                } catch (e: Exception) {
                    LOGGER.warn("Can not get updates for telegram bot", e)
                    emptyList()
                }

        var newLastSeq: Long? = lastSeq

        updates.forEach { update ->
            newLastSeq = max(newLastSeq ?: Long.MIN_VALUE, update.id + 1)
            onUpdateHandlers.forEach { handler ->
                coroutineScope.launch(fetcherThreadContext) {
                    try {
                        handler.onUpdate(update)
                    } catch (e: Exception) {
                        LOGGER.error("Can not execute 'onUpdate' for handler: ${handler.javaClass}", e)
                    }
                }
            }
        }

        if (newLastSeq != lastSeq) {
            updateSequenceRepository.saveAndFlush(UpdateSequenceDto(newLastSeq!!, DEFAULT_ID))
        }

        return newLastSeq
    }


    @PreDestroy
    fun preDestroy() {
        isContinue.set(false)
        try {
            if (fetcherThread.isAlive) {
                fetcherThread.join(DEFAULT_TIMEOUT_STOPPING)
                fetcherThread.interrupt()
                fetcherThread.join(DEFAULT_TIMEOUT_INTERRUPT)
            }
        } catch (e: Exception) {
            LOGGER.error("Can not stop fetcher thread", e)
        }
    }

    companion object {
        private const val DEFAULT_TIMEOUT_STOPPING: Long = 5000
        private const val DEFAULT_TIMEOUT_INTERRUPT: Long = 5000

        private val LOGGER: Logger = LoggerFactory.getLogger(TelegramUpdateFetcher::class.java)
    }
}