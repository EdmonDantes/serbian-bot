package ru.loginov.telegram.api.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import ru.loginov.telegram.api.entity.BotCommandScopeType

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class GetMyCommandsRequest {
    /**
     * Describing scope of users for which the commands are relevant
     */
    @JsonProperty(value = "scope", required = false)
    var scope: BotCommandScopeType? = null

    /**
     * A two-letter ISO 639-1 language code.
     * If empty, commands will be applied to all users from the given scope,
     * for whose language there are no dedicated commands
     */
    @JsonProperty(value = "language_code", required = false)
    var language: String? = null
}