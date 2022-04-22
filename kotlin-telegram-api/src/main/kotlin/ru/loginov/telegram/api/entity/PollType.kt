package ru.loginov.telegram.api.entity

import com.fasterxml.jackson.annotation.JsonValue

enum class PollType(@JsonValue val id: String) {
    REGULAR("regular"),
    QUIZ("quiz")
    ;

}