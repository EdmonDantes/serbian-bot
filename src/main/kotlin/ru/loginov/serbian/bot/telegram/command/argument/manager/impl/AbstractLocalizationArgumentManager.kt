package ru.loginov.serbian.bot.telegram.command.argument.manager.impl

import io.github.edmondantes.simple.localization.LocalizationRequest
import io.github.edmondantes.simple.localization.impl.singleRequest
import ru.loginov.serbian.bot.telegram.command.argument.AnyArgument
import ru.loginov.serbian.bot.telegram.command.argument.manager.ArgumentManager
import ru.loginov.serbian.bot.telegram.command.argument.manager.LocalizationArgumentManager

abstract class AbstractLocalizationArgumentManager<T>(
        private val parent: ArgumentManager<T>
) : LocalizationArgumentManager {

    override fun choose(name: String, message: LocalizationRequest?): AnyArgument<Boolean> =
            parent.choose(name, message?.let { prepareMessage(it) } ?: transformMessage(DEFAULT_MESSAGE_FOR_CHOOSE))

    override fun language(name: String, message: LocalizationRequest?): AnyArgument<String> =
            parent.language(name, message?.let { prepareMessage(it) } ?: transformMessage(DEFAULT_MESSAGE_FOR_CHOOSE))

    override fun location(name: String, message: LocalizationRequest?): AnyArgument<Pair<Double, Double>> =
            parent.location(name, message?.let { prepareMessage(it) } ?: transformMessage(DEFAULT_MESSAGE_FOR_WRITE))

    override fun argument(name: String, message: LocalizationRequest?): AnyArgument<String> =
            parent.argument(name, message?.let { prepareMessage(it) } ?: transformMessage(DEFAULT_MESSAGE_FOR_WRITE))

    override fun argument(
            name: String,
            variants: List<LocalizationRequest>,
            message: LocalizationRequest?
    ): AnyArgument<String> =
            parent.argument(
                    name,
                    variants.mapNotNull { prepareMessage(it) },
                    message?.let { prepareMessage(it) } ?: transformMessage(DEFAULT_MESSAGE_FOR_WRITE)
            )

    override fun argument(
            name: String,
            variants: Map<LocalizationRequest, String>,
            message: LocalizationRequest?
    ): AnyArgument<String> =
            parent.argument(
                    name,
                    variants.mapNotNull { (key, value) -> prepareMessage(key)?.let { it to value } }.toMap(),
                    message?.let { prepareMessage(it) } ?: transformMessage(DEFAULT_MESSAGE_FOR_WRITE)
            )


    protected abstract fun prepareMessage(message: LocalizationRequest?): T?
    protected abstract fun transformMessage(message: LocalizationRequest): T?

    companion object {
        private val DEFAULT_MESSAGE_FOR_WRITE = singleRequest("bot.abstract.command.please.write.argument")
        private val DEFAULT_MESSAGE_FOR_CHOOSE = singleRequest("bot.abstract.command.please.choose.argument")
    }
}