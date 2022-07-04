package ru.loginov.serbian.bot.telegram.command.argument.manager

import ru.loginov.serbian.bot.telegram.command.argument.AnyArgument

interface ArgumentManager<T> {
    /**
     * Show to user menu with 2 choose (Yes or No)
     * Return true of false
     */
    fun choose(name: String, message: T? = null): AnyArgument<Boolean>

    /**
     * Show to user menu with supported language
     * Return language code
     */
    fun language(name: String, message: T? = null): AnyArgument<String>

    /**
     * Waiting for location from user
     */
    fun location(name: String, message: T? = null): AnyArgument<Pair<Double, Double>>

    /**
     * Return next argument.
     */
    fun argument(name: String, message: T? = null): AnyArgument<String>

    /**
     * Return next argument
     * Returned argument will be one of [variants]
     */
    fun argument(name: String, variants: List<T>, message: T? = null): AnyArgument<String>

    /**
     * Return next argument
     * Returned argument will be one of [variants] values
     * User can see only keys of [variants]
     */
    fun argument(name: String, variants: Map<T, String>, message: T? = null): AnyArgument<String>
}