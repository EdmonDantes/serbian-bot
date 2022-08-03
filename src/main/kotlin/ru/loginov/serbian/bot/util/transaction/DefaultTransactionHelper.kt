package ru.loginov.serbian.bot.util.transaction

import org.springframework.stereotype.Component

@Component
class DefaultTransactionHelper : TransactionHelper {

    override fun <T> transaction(action: () -> T): T = action()
}