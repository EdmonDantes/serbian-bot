package ru.loginov.telegram.api.util

import ru.loginov.telegram.api.entity.MessageEntity
import ru.loginov.telegram.api.entity.MessageEntityType
import ru.loginov.telegram.api.entity.User
import ru.loginov.telegram.api.entity.builder.messageEntity
import java.net.URL

open class TelegramMessageTextBuilder {

    private val stringBuilder = StringBuilder()
    private val stringWithoutFormatting = StringBuilder()
    private val entities = ArrayList<MessageEntity>()

    open fun append(obj: Any, clearFormatting: Boolean = true): TelegramMessageTextBuilder = this.apply {
        if (obj is TelegramMessageTextBuilder) {
            val startPosition = stringWithoutFormatting.length
            stringBuilder.append(obj.stringBuilder)
            stringWithoutFormatting.append(obj.stringWithoutFormatting)
            obj.entities.forEach {
                entities.add(it.copy(offset = it.offset + startPosition))
            }
        } else {
            val str = obj.toString()
            appendString(str, clearFormatting)
        }
    }

    open fun bold(block: TelegramMessageTextBuilder.() -> Unit): TelegramMessageTextBuilder = this.apply {
        processBlock(block, "*", MessageEntityType.BOLD)
    }

    open fun italic(block: TelegramMessageTextBuilder.() -> Unit): TelegramMessageTextBuilder = this.apply {
        processBlock(block, "_", MessageEntityType.ITALIC)
    }

    open fun underline(block: TelegramMessageTextBuilder.() -> Unit): TelegramMessageTextBuilder = this.apply {
        processBlock(block, "__", MessageEntityType.UNDERLINE)
    }

    open fun strikethrough(block: TelegramMessageTextBuilder.() -> Unit): TelegramMessageTextBuilder = this.apply {
        processBlock(block, "~", MessageEntityType.STRIKETHROUGH)
    }

    open fun spoiler(block: TelegramMessageTextBuilder.() -> Unit): TelegramMessageTextBuilder = this.apply {
        processBlock(block, "||", MessageEntityType.SPOILER)
    }

    open fun url(url: URL): TelegramMessageTextBuilder = this.apply {
        val str = url.toString()
        stringBuilder.append(str)

        entities.add(MessageEntity(MessageEntityType.URL, stringWithoutFormatting.length.toLong(), str.length.toLong()))
        stringWithoutFormatting.append(str)
    }

    open fun link(name: String, url: URL): TelegramMessageTextBuilder = this.apply {
        stringBuilder.append("[").append(name).append("](").append(url).append(")")

        entities.add(
                MessageEntity(
                        MessageEntityType.TEXT_LINK,
                        stringWithoutFormatting.length.toLong(),
                        name.length.toLong(),
                        url = url.toString()
                )
        )
        stringWithoutFormatting.append(name)
    }

    open fun link(name: String, url: String): TelegramMessageTextBuilder = this.apply {
        stringBuilder.append("[").append(name).append("](").append(url).append(")")

        entities.add(
                MessageEntity(
                        MessageEntityType.TEXT_LINK,
                        stringWithoutFormatting.length.toLong(),
                        name.length.toLong(),
                        url = url
                )
        )
        stringWithoutFormatting.append(name)
    }

    open fun mention(user: User): TelegramMessageTextBuilder = this.apply {
        if (user.username.isNullOrEmpty()) {
            return@apply
        }

        stringBuilder.append("@").append(user.username)
        entities.add(
                MessageEntity(
                        MessageEntityType.MENTION,
                        stringWithoutFormatting.length.toLong(),
                        user.username.length.toLong()
                )
        )
        stringWithoutFormatting.append("@").append(user.username)
    }

    open fun hashtag(hashtag: String): TelegramMessageTextBuilder = this.apply {
        stringBuilder.append("#").append(hashtag)
        entities.add(
                MessageEntity(
                        MessageEntityType.HASHTAG,
                        stringWithoutFormatting.length.toLong(),
                        hashtag.length.toLong()
                )
        )
        stringWithoutFormatting.append(hashtag)
    }

    open fun email(email: String) {
        stringBuilder.append(email)
        entities.add(
                MessageEntity(
                        MessageEntityType.EMAIL,
                        stringWithoutFormatting.length.toLong(),
                        email.length.toLong()
                )
        )
        stringWithoutFormatting.append(email)
    }

    open fun phone(phoneNumber: String) {
        stringBuilder.append(phoneNumber)
        entities.add(
                MessageEntity(
                        MessageEntityType.PHONE_NUMBER,
                        stringWithoutFormatting.length.toLong(),
                        phoneNumber.length.toLong()
                )
        )
        stringWithoutFormatting.append(phoneNumber)
    }

    open fun textMention(name: String, user: User): TelegramMessageTextBuilder = this.apply {
        stringBuilder.append("[").append(name).append("](tg://user?id=").append(user.id).append(")")
        entities.add(
                MessageEntity(
                        MessageEntityType.TEXT_MENTIONS,
                        stringWithoutFormatting.length.toLong(),
                        name.length.toLong(),
                        user = user
                )
        )
        stringWithoutFormatting.append(name)
    }

    open fun code(block: StringBuilder.() -> Unit): TelegramMessageTextBuilder = this.apply {
        val codeBlockBuilder = StringBuilder()
        block(codeBlockBuilder)

        if (codeBlockBuilder.contains('\n')) {
            throw IllegalArgumentException("Can not use '\\n' in one line code block")
        }

        stringBuilder.append('`').append(transformToWithoutFormatting(codeBlockBuilder, true)).append('`')
        entities.add(messageEntity { code().setStartPosition(stringWithoutFormatting.length).setLength(codeBlockBuilder.length) })
        stringWithoutFormatting.append(codeBlockBuilder)
    }

    open fun codeBlock(block: StringBuilder.() -> Unit): TelegramMessageTextBuilder = this.apply {
        codeBlock(null, block)
    }

    open fun codeBlock(language: String?, block: StringBuilder.() -> Unit): TelegramMessageTextBuilder = this.apply {
        val codeBlockBuilder = StringBuilder()
        block(codeBlockBuilder)

        if (stringBuilder.last() != '\n') {
            stringBuilder.append('\n')
        }
        stringBuilder.append("```")
        if (language != null) {
            stringBuilder.append(language)
        }
        stringBuilder.append('\n')
                .append(transformToWithoutFormatting(codeBlockBuilder, true))
        if (stringBuilder.last() != '\n') {
            stringBuilder.append('\n')
        }
        stringBuilder.append("```")

        entities.add(messageEntity {
            codeBlock(language).setStartPosition(stringWithoutFormatting.length).setLength(
                    codeBlockBuilder.length
            )
        })
        stringWithoutFormatting.append(codeBlockBuilder)
    }

    open fun toMarkdownV2String(): String = stringBuilder.toString()

    open fun toEntities(): Pair<String, List<MessageEntity>> = stringWithoutFormatting.toString() to entities

    open protected fun appendString(str: String, clearFormatting: Boolean) {
        stringBuilder.append(if (clearFormatting) transformToWithoutFormatting(str) else str)
        stringWithoutFormatting.append(str)
    }

    open protected fun processBlock(
            block: TelegramMessageTextBuilder.() -> Unit,
            markdownSymbols: String,
            type: MessageEntityType
    ) {
        stringBuilder.append(markdownSymbols)
        val startPosition = stringWithoutFormatting.length
        block(this)
        val endPosition = stringWithoutFormatting.length
        stringBuilder.append(markdownSymbols)

        entities.add(
                MessageEntity(
                        type,
                        startPosition.toLong(),
                        (endPosition - startPosition).toLong()
                )
        )
    }

    open protected fun transformToWithoutFormatting(str: CharSequence, inCodeBlock: Boolean = false): String =
            str.replace(if (inCodeBlock) CODE_SYMBOLS else MARKDOWN_SYMBOLS) { mr -> "\\${mr.value}" }


    companion object {
        private val MARKDOWN_SYMBOLS = Regex("[\\\\_*\\[\\]()~`>#+-=|{}.!]")
        private val CODE_SYMBOLS = Regex("`\\\\")

        fun fromString(str: String): TelegramMessageTextBuilder = TelegramMessageTextBuilder().apply { append(str) }
    }
}

suspend fun markdown2Coroutine(block: suspend TelegramMessageTextBuilder.() -> Unit): TelegramMessageTextBuilder = TelegramMessageTextBuilder().apply {
    block(this)
}

suspend fun markdown2StringCoroutine(block: suspend TelegramMessageTextBuilder.() -> Unit): String = TelegramMessageTextBuilder().apply {
    block(this)
}.toMarkdownV2String()

fun markdown2(block: TelegramMessageTextBuilder.() -> Unit): TelegramMessageTextBuilder = TelegramMessageTextBuilder().apply {
    block(this)
}

fun markdown2String(block: TelegramMessageTextBuilder.() -> Unit): String = TelegramMessageTextBuilder().apply {
    block(this)
}.toMarkdownV2String()

fun markdown2FromString(str: String): TelegramMessageTextBuilder = TelegramMessageTextBuilder.fromString(str)