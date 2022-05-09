package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object represents the scope to which bot commands are applied.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class BotCommandScope(
        /**
         * Scope type
         */
        @JsonProperty(value = "type", required = true) val type: BotCommandScopeType,
        /**
         * Support only for [BotCommandScopeType.CHAT],[BotCommandScopeType.CHAT_ADMINISTRATORS],[BotCommandScopeType.CHAT_MEMBER]
         * Unique identifier for the target chat.
         *
         * *Optional*
         */
        @JsonProperty(value = "chat_id", required = false) val chatId: Long?,
        /**
         *  Support only for [BotCommandScopeType.CHAT_MEMBER]
         * 	Unique identifier of the target user
         *
         * 	*Optional*
         */
        @JsonProperty(value = "user_id", required = false) val userId: Long?
)