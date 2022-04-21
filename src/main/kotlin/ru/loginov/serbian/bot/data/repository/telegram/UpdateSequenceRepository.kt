package ru.loginov.serbian.bot.data.repository.telegram

import org.springframework.data.jpa.repository.JpaRepository
import ru.loginov.serbian.bot.data.dao.telegram.UpdateSequence

interface UpdateSequenceRepository : JpaRepository<UpdateSequence, Long> {
    companion object {
        const val DEFAULT_ID: Long = 1
    }
}