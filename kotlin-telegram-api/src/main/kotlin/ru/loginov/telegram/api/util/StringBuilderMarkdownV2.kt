package ru.loginov.telegram.api.util

import ru.loginov.telegram.api.entity.MessageEntity
import ru.loginov.telegram.api.entity.MessageEntityType
import ru.loginov.telegram.api.entity.User
import ru.loginov.telegram.api.entity.builder.messageEntity
import java.net.URL

class StringBuilderMarkdownV2 {

    private val stringBuilder = StringBuilder()
    private val stringWithoutFormatting = StringBuilder()
    private val entities = ArrayList<MessageEntity>()

    fun append(obj: Any, clearFormatting: Boolean = true): StringBuilderMarkdownV2 = this.apply {
        if (obj is StringBuilderMarkdownV2) {
            val startPosition = stringWithoutFormatting.length
            stringBuilder.append(obj.stringBuilder)
            stringWithoutFormatting.append(obj.stringWithoutFormatting)
            obj.entities.forEach {
                entities.add(it.copy(offset = it.offset + startPosition))
            }
        } else {
            val str = obj.toString()
            stringBuilder.append(if (clearFormatting) transformToWithoutFormatting(str) else str)
            stringWithoutFormatting.append(str)
        }
    }

    fun bold(block: StringBuilderMarkdownV2.() -> Unit): StringBuilderMarkdownV2 = this.apply {
        processBlock(block, "*", MessageEntityType.BOLD)
    }

    fun italic(block: StringBuilderMarkdownV2.() -> Unit): StringBuilderMarkdownV2 = this.apply {
        processBlock(block, "_", MessageEntityType.ITALIC)
    }

    fun underline(block: StringBuilderMarkdownV2.() -> Unit): StringBuilderMarkdownV2 = this.apply {
        processBlock(block, "__", MessageEntityType.UNDERLINE)
    }

    fun strikethrough(block: StringBuilderMarkdownV2.() -> Unit): StringBuilderMarkdownV2 = this.apply {
        processBlock(block, "~", MessageEntityType.STRIKETHROUGH)
    }

    fun spoiler(block: StringBuilderMarkdownV2.() -> Unit): StringBuilderMarkdownV2 = this.apply {
        processBlock(block, "||", MessageEntityType.SPOILER)
    }

    fun url(url: URL): StringBuilderMarkdownV2 = this.apply {
        val str = url.toString()
        stringBuilder.append(str)

        entities.add(MessageEntity(MessageEntityType.URL, stringWithoutFormatting.length.toLong(), str.length.toLong()))
        stringWithoutFormatting.append(str)
    }

    fun link(name: String, url: URL): StringBuilderMarkdownV2 = this.apply {
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

    fun mention(user: User): StringBuilderMarkdownV2 = this.apply {
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

    fun hashtag(hashtag: String): StringBuilderMarkdownV2 = this.apply {
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

    fun email(email: String) {
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

    fun phone(phoneNumber: String) {
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

    fun textMention(name: String, user: User): StringBuilderMarkdownV2 = this.apply {
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

    fun code(block: StringBuilder.() -> Unit): StringBuilderMarkdownV2 = this.apply {
        val codeBlockBuilder = StringBuilder()
        block(codeBlockBuilder)

        if (codeBlockBuilder.contains('\n')) {
            throw IllegalArgumentException("Can not use '\\n' in one line code block")
        }

        stringBuilder.append('`').append(transformToWithoutFormatting(codeBlockBuilder, true)).append('`')
        entities.add(messageEntity { code().setStartPosition(stringWithoutFormatting.length).setLength(codeBlockBuilder.length) })
        stringWithoutFormatting.append(codeBlockBuilder)
    }

    fun codeBlock(block: StringBuilder.() -> Unit): StringBuilderMarkdownV2 = this.apply {
        codeBlock(null, block)
    }

    fun codeBlock(language: String?, block: StringBuilder.() -> Unit): StringBuilderMarkdownV2 = this.apply {
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

    fun toMarkdownV2String(): String = stringBuilder.toString()

    fun toEntitiesWay(): Pair<String, List<MessageEntity>> = stringWithoutFormatting.toString() to entities

    private fun processBlock(
            block: StringBuilderMarkdownV2.() -> Unit,
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

    private fun transformToWithoutFormatting(str: CharSequence, inCodeBlock: Boolean = false): String =
            str.replace(if (inCodeBlock) CODE_SYMBOLS else MARKDOWN_SYMBOLS) { mr -> "\\${mr.value}" }


    companion object {
        private val MARKDOWN_SYMBOLS = Regex("[\\\\_*\\[\\]()~`>#+-=|{}.!]")
        private val CODE_SYMBOLS = Regex("`\\\\")

        fun fromString(str: String): StringBuilderMarkdownV2 = StringBuilderMarkdownV2().apply { append(str) }
    }
}

fun markdown2(block: StringBuilderMarkdownV2.() -> Unit): StringBuilderMarkdownV2 = StringBuilderMarkdownV2().apply {
    block(this)
}

fun markdown2FromString(str: String) : StringBuilderMarkdownV2 = StringBuilderMarkdownV2.fromString(str)