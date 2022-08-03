package ru.loginov.serbian.bot.util.transaction

import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface TransactionHelper {

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    fun <T> transaction(action: () -> T): T

}