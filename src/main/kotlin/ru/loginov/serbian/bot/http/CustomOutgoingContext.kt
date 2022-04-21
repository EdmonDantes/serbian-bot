package ru.loginov.serbian.bot.http

import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent

class CustomOutgoingContext(
        override val contentType: ContentType?,
        private val bytes: ByteArray
) : OutgoingContent.ByteArrayContent() {
    override fun bytes(): ByteArray = bytes
}