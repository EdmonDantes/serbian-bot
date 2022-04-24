package ru.loginov.serbian.bot.telegram.command.context.impl

import ru.loginov.serbian.bot.telegram.command.context.BotCommandArgumentManager

class ParametersBotCommandArgumentManager(
        private val parent: BotCommandArgumentManager?,
        messageWithoutCommand: String
) : BotCommandArgumentManager {

    private val arguments: List<String>
    private var index: Int = 0

    init {
        val arguments = ArrayList<String>()
        var builder = StringBuilder()
        var ignoreSpaces = false
        messageWithoutCommand.forEachIndexed { index, ch ->
            if (ch == '\'' && (index == 0 || messageWithoutCommand[index - 1] != '\\')) {
                if (ignoreSpaces) {
                    arguments.add(builder.toString())
                    builder = StringBuilder()
                } else {
                    ignoreSpaces = true
                }
            } else if (ch == ' ') {
                if (ignoreSpaces) {
                    builder.append(ch)
                } else if (builder.isNotEmpty()) {
                    arguments.add(builder.toString())
                    builder = StringBuilder()
                }
            } else {
                builder.append(ch)
            }
        }

        this.arguments = arguments
    }

    override suspend fun getNextArgument(): String =
            if (index < arguments.size) {
                arguments[index++]
            } else {
                parent?.getNextArgument()
                        ?: throw IndexOutOfBoundsException("Current index '$index', but size '${arguments.size}'")
            }

    override suspend fun getNextArgument(name: String, description: String?): String =
            if (index < arguments.size) {
                arguments[index++]
            } else {
                parent?.getNextArgument(name, description)
                        ?: throw IndexOutOfBoundsException("Current index '$index', but size '${arguments.size}'")
            }

    override suspend fun getNextArgument(variants: List<String>, description: String?): String? {
        TODO("Not yet implemented")
    }

    override suspend fun getNextArgument(variants: Map<String, String>, description: String?): String? {
        TODO("Not yet implemented")
    }
}