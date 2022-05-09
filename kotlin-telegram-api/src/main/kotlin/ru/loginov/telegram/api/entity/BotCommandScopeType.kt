package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue

/**
 * Scope type
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
enum class BotCommandScopeType(@JsonValue val type: String) {
    /**
     * Represents the default scope of bot commands.
     * Default commands are used if no commands with a narrower scope are specified for the user.
     */
    DEFAULT("default"),

    /**
     * Represents the scope of bot commands, covering all private chats.
     */
    ALL_PRIVATE_CHAT("all_private_chats"),

    /**
     * Represents the scope of bot commands, covering all group and supergroup chats.
     */
    ALL_GROUP_CHATS("all_group_chats"),

    /**
     * Represents the scope of bot commands, covering all group and supergroup chat administrators.
     */
    ALL_CHAT_ADMINISTRATORS("all_chat_administrators"),

    /**
     * Represents the scope of bot commands, covering a specific chat.
     */
    CHAT("chat"),

    /**
     * Represents the scope of bot commands, covering all administrators of a specific group or supergroup chat.
     */
    CHAT_ADMINISTRATORS("chat_administrators"),

    /**
     * Represents the scope of bot commands, covering a specific member of a group or supergroup chat.
     */
    CHAT_MEMBER("chat_member")
}