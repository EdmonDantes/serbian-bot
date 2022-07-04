package ru.loginov.telegram.api.util.impl

import ru.loginov.telegram.api.util.Markdown2StringBuilder

open class DefaultMarkdown2StringBuilder : AbstractMarkdown2StringBuilder() {
    override val codeBlockEscapedSymbols: CharArray = CODE_BLOCK_ESCAPED_SYMBOLS

    override fun append(char: Char, needToEscape: Boolean): Markdown2StringBuilder = this.apply {
        if (needToEscape && MARKDOWN_ESCAPED_SYMBOLS.contains(char)) {
            markdown2.append('\\')
        }
        markdown2.append(char)
    }

    override fun append(str: String, needToEscape: Boolean): Markdown2StringBuilder = this.apply {
        if (needToEscape) {
            val escaped = StringBuilder(str).replace(MARKDOWN_ESCAPED_SYMBOLS) { ch -> "\\$ch" }.toString()
            markdown2.append(escaped)
        } else {
            markdown2.append(str)
        }
    }

    override fun append(obj: Any, needToEscape: Boolean): Markdown2StringBuilder =
            append(obj.toString(), needToEscape)

    companion object {
        private val MARKDOWN_ESCAPED_SYMBOLS = charArrayOf(
                '\\',
                '_',
                '*',
                '[',
                ']',
                '(',
                ')',
                '~',
                '`',
                '>',
                '#',
                '+',
                '-',
                '=',
                '|',
                '{',
                '}',
                '.',
                '!'
        )

        private val CODE_BLOCK_ESCAPED_SYMBOLS = charArrayOf('`', '\\')
    }
}

fun markdown2(): Markdown2StringBuilder = DefaultMarkdown2StringBuilder()

inline fun markdown2(block: Markdown2StringBuilder.() -> Unit): Markdown2StringBuilder =
        DefaultMarkdown2StringBuilder().apply(block)