package ru.loginov.serbian.bot.telegram.command.context

interface BotCommandArgumentManager {

    /**
     * Show to user menu with 2 choose (Yes or No)
     * Return true of false
     */
    suspend fun getNextChooseArgument(message: String?): Boolean

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

suspend fun BotCommandArgumentManager.getNextArgument(
        message: String?,
        invalidResultMessage: (String?) -> String?,
        optional: Boolean = false,
        validator: (String?) -> Boolean
): String? {
    var result = getNextArgument(message, optional)
    while (!validator.invoke(result)) {
        result = getNextArgument(invalidResultMessage(result), optional)
    }
    return result
}

suspend fun BotCommandArgumentManager.getNextArgument(
        message: String?,
        invalidResultMessage: String?,
        optional: Boolean = false,
        validator: (String?) -> Boolean
): String? =
        getNextArgument(message, { invalidResultMessage }, optional, validator)