package ru.loginov.serbian.bot.telegram.command.context

interface BotCommandArgumentManager {

    /**
     * Return next argument.
     */
    suspend fun getNextArgument(message: String?, optional: Boolean = false): String?

    /**
     * Return next argument
     * Returned argument will be one of [variants]
     */
    suspend fun getNextArgument(variants: List<String>, message: String? = null, optional: Boolean = false): String?

    /**
     * Return next argument
     * Returned argument will be one of [variants] values
     * User can see only keys of [variants]
     */
    suspend fun getNextArgument(
            variants: Map<String, String>,
            message: String? = null,
            optional: Boolean = false
    ): String?
}