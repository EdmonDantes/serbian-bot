package ru.loginov.simple.localization.parser.impl

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.loginov.simple.localization.exception.NotEscapedCharacterException

class DefaultLocalizationRequestParserTest {

    private val parser = DefaultLocalizationRequestParser()

    @Test
    fun onlyLocalizationKey() {
        val str = "@{localizationKey}"
        val request = parser.parse(str)

        Assertions.assertEquals(1, request.keys.size)

        val key = request.keys.first()
        Assertions.assertEquals("localizationKey", key.key)
        Assertions.assertEquals(0, key.arguments.size)
    }

    @Test
    fun onlyLocalizationKeyWithEscape() {
        val str = "@{\\{localizationKey}"

        val request = parser.parse(str)

        Assertions.assertEquals(1, request.keys.size)

        val key = request.keys.first()
        Assertions.assertEquals("{localizationKey", key.key)
        Assertions.assertEquals(0, key.arguments.size)
    }

    @Test
    fun emptyLocalizationKey() {
        val str = "@{}"
        assertThrows<IllegalStateException>("Localization key can not be empty. Match string '$str' in indexes '0..${str.lastIndex}'") {
            parser.parse(str)
        }
    }

    @Test
    fun notAllEscapedCase1() {
        val str = "@{{localizationKey}"
        assertThrows<NotEscapedCharacterException>("Found not escaped characters. Please check you string. Match string '$str' in indexes '0..${str.lastIndex}'. Not escaped characters on '2..2'") {
            parser.parse(str)
        }
    }

    @Test
    fun notAllEscapedCase2() {
        val str = "@{localizationKey@}"
        assertThrows<NotEscapedCharacterException>("Found not escaped characters. Please check you string. Match string '$str' in indexes '0..${str.lastIndex}'. Not escaped characters on '17..17'") {
            parser.parse(str)
        }
    }

    @Test
    fun localizationKeyWithArguments() {
        val str = "@{localizationKey}{arg1}{arg2}"
        val request = parser.parse(str)

        Assertions.assertEquals(1, request.keys.size)

        val key = request.keys.first()
        Assertions.assertEquals("localizationKey", key.key)
        Assertions.assertEquals(2, key.arguments.size)
        Assertions.assertEquals("arg1", key.arguments[0])
        Assertions.assertEquals("arg2", key.arguments[1])
    }

    @Test
    fun localizationKeyWithArgumentsWithEscape() {
        val str = "@{localizationKey}{arg1\\}}{arg2\\{}"
        val request = parser.parse(str)

        Assertions.assertEquals(1, request.keys.size)

        val key = request.keys.first()
        Assertions.assertEquals("localizationKey", key.key)
        Assertions.assertEquals(2, key.arguments.size)
        Assertions.assertEquals("arg1}", key.arguments[0])
        Assertions.assertEquals("arg2{", key.arguments[1])
    }

    @Test
    fun localizationKeyWithString() {
        val str = "start@{localizationKey}end"
        val request = parser.parse(str)

        Assertions.assertEquals(3, request.keys.size)

        Assertions.assertEquals("", request.keys[0].key)
        Assertions.assertEquals(1, request.keys[0].arguments.size)
        Assertions.assertEquals("start", request.keys[0].arguments[0])

        Assertions.assertEquals("localizationKey", request.keys[1].key)
        Assertions.assertEquals(0, request.keys[1].arguments.size)

        Assertions.assertEquals("", request.keys[2].key)
        Assertions.assertEquals(1, request.keys[2].arguments.size)
        Assertions.assertEquals("end", request.keys[2].arguments[0])
    }

    @Test
    fun localizationKeyWithStringWithEscape() {
        val str = "start\\@@{localizationKey}end\\@\\{\\}"
        val request = parser.parse(str)

        Assertions.assertEquals(3, request.keys.size)

        Assertions.assertEquals("", request.keys[0].key)
        Assertions.assertEquals(1, request.keys[0].arguments.size)
        Assertions.assertEquals("start@", request.keys[0].arguments[0])

        Assertions.assertEquals("localizationKey", request.keys[1].key)
        Assertions.assertEquals(0, request.keys[1].arguments.size)

        Assertions.assertEquals("", request.keys[2].key)
        Assertions.assertEquals(1, request.keys[2].arguments.size)
        Assertions.assertEquals("end@{}", request.keys[2].arguments[0])
    }

}