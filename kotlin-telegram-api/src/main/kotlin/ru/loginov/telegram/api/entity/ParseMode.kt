package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
enum class ParseMode(@JsonValue val value: String) {
    MARKDOWN2("MarkdownV2"),
    HTML("HTML"),
    MARKDOWN("Markdown"),
    ;
}