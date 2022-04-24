package ru.loginov.serbian.bot.telegram.command.context

interface BotCommandArgumentManager {

    /**
     * Return next argument.
     * Please use [getNextArgument] with name
     */
    suspend fun getNextArgument() : String?

    /**
     * Return next argument
     */
    suspend fun getNextArgument(name: String, description: String? = null) : String?

    /**
     * Return next argument
     * Returned argument will be one of [variants]
     */
    suspend fun getNextArgument(variants: List<String>, description: String? = null) : String?

    /**
     * Return next argument
     * Returned argument will be one of [variants] values
     * User can see only keys of [variants]
     */
    suspend fun getNextArgument(variants: Map<String, String>, description: String? = null) : String?
}