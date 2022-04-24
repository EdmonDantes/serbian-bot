package ru.loginov.telegram.api.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents request for method [ru.loginov.telegram.api.TelegramAPI.answerCallbackQuery]
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class AnswerCallbackQueryRequest {

    /**
     * Unique identifier for the query to be answered
     */
    @JsonProperty(value = "callback_query_id", required = true)
    var callbackQueryId: String? = null

    /**
     * Text of the notification. If not specified, nothing will be shown to the user, 0-200 characters
     *
     * *Optional*
     */
    @JsonProperty(value = "text", required = false)
    var text: String? = null

    /**
     * If True, an alert will be shown by the client instead of a notification at the top of the chat screen.
     * Defaults to false.
     *
     * *Optional*
     */
    @JsonProperty(value = "show_alert", required = false)
    var showAlert: Boolean? = null

    /**
     * URL that will be opened by the user's client.
     * If you have created a Game and accepted the conditions via @Botfather, specify the URL that opens your game
     *
     * Otherwise, you may use links like t.me/your_bot?start=XXXX that open your bot with a parameter.
     *
     * *Optional*
     */
    @JsonProperty(value = "url", required = false)
    var url: String? = null

    /**
     * The maximum amount of time in seconds that the result of the callback query may be cached client-side.
     * Telegram apps will support caching starting in version 3.14.
     *
     * Defaults to 0.
     *
     * *Optional*
     */
    @JsonProperty(value = "cache_time", required = false)
    var cacheTime: Long? = null

}