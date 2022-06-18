package ru.loginov.serbian.bot.telegram.command.argument.manager

import ru.loginov.serbian.bot.telegram.command.argument.AnyArgument

interface ArgumentManager {
    /**
     * Show to user menu with 2 choose (Yes or No)
     * Return true of false
     */
    fun choose(name: String, message: String? = null): AnyArgument<Boolean>

    /**
     * Show to user menu with supported language
     * Return language code
     */
    fun language(name: String, message: String? = null): AnyArgument<String>

    /**
     * Waiting for location from user
     */
    fun location(name: String, message: String? = null): AnyArgument<Pair<Double, Double>>

    /**
     * Return next argument.
     */
    fun argument(name: String, message: String? = null): AnyArgument<String>

    /**
     * Return next argument
     * Returned argument will be one of [variants]
     */
    fun argument(name: String, variants: List<String>, message: String? = null): AnyArgument<String>

    /**
     * Return next argument
     * Returned argument will be one of [variants] values
     * User can see only keys of [variants]
     */
    fun argument(name: String, variants: Map<String, String>, message: String? = null): AnyArgument<String>
}