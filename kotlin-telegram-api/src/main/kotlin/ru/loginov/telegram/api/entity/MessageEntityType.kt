package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonValue

enum class MessageEntityType(@JsonValue private val tag: String) {
    /**
     * Example: @username
     */
    MENTION("mention"),

    /**
     * Example: #hashtag
     */
    HASHTAG("hashtag"),

    /**
     * Example: $USD
     */
    CASHTAG("cashtag"),

    /**
     * Example: /start@jobs_bot
     */
    BOT_COMMAND("bot_command"),

    /**
     * Example: `https://telegram.org`
     */
    URL("url"),

    /**
     * Example: do-not-reply@telegram.org
     */
    EMAIL("email"),

    /**
     * Example: +1-212-555-0123
     */
    PHONE_NUMBER("phone_number"),

    /**
     * Bold text
     */
    BOLD("bold"),

    /**
     * Italic text
     */
    ITALIC("italic"),

    /**
     * Underlined text
     */
    UNDERLINE("underline"),

    /**
     * Strikethrough text
     */
    STRIKETHROUGH("strikethrough"),

    /**
     * Spoiler message
     */
    SPOILER("spoiler"),

    /**
     * Monowidth string
     */
    CODE("code"),

    /**
     * Monowidth block
     */
    PRE("pre"),

    /**
     * For clickable text URLs
     */
    TEXT_LINK("text_link"),

    /**
     * For users without usernames
     */
    TEXT_MENTIONS("text_mention");
}