package ru.loginov.serbian.bot.spring.localization

import org.slf4j.LoggerFactory
import ru.loginov.serbian.bot.spring.localization.annotation.Localized
import ru.loginov.simple.localization.context.LocalizationContext
import ru.loginov.simple.localization.impl.localizationKey
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Parameter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

//TODO: Need to test
class LocalizedProxyHandler(private val obj: Any?, private val clazz: Class<*>) : InvocationHandler {

    private val LOGGER = LoggerFactory.getLogger(clazz)
    private val methodNamesForProcessing: Set<String> = clazz.methods
            .filter {
                Modifier.isPublic(it.modifiers)
                        && it.parameterTypes.find { it is LocalizationContext } != null
                        && it.parameters.any { isParameterShouldLocalized(it) }
            }
            .map { it.name }
            .toSet()

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any?>?): Any? {
        method ?: return null
        val params = args ?: emptyArray()

        if (method.parameterCount != params.size) {
            throw IllegalStateException(
                    "Can not execute method '${method.name}' in class '$clazz' " +
                            "because parameters count in contract and in runtime is different. " +
                            "Parameters count in contract '${method.parameterCount}'. " +
                            "Parameters count in runtime '${params.size}'"
            )
        }

        if (!methodNamesForProcessing.contains(method.name) || !Modifier.isPublic(method.modifiers)) {
            return method.invoke(obj, *params)
        }

        val context = params.find { it is LocalizationContext } as? LocalizationContext
                ?: return method.invoke(obj, *params)

        val newParams = Array<Any?>(params.size) { null }

        for (i in method.parameters.indices) {
            val parameter = method.parameters[i]
            val paramValue = params[i]

            if (!isParameterShouldLocalized(parameter) || paramValue == null) {
                newParams[i] = params[i]
                continue
            }

            if (paramValue !is String && paramValue !is Iterable<*>) {
                throw IllegalStateException(
                        "Parameter '${parameter.name}' in method '${method.name}' " +
                                "in class '$clazz' should be String, but it is '${paramValue.javaClass}'"
                )
            }

            val translated: Any?

            if (paramValue is Iterable<*>) {
                val iterator = paramValue.iterator()
                if (!iterator.hasNext()) {
                    newParams[i] = paramValue
                    continue
                }

                translated = ArrayList<String>()

                while (iterator.hasNext()) {
                    val value = iterator.next()
                    if (value !is String) {
                        throw IllegalStateException(
                                "Parameter '${parameter.name}' in method '${method.name}' " +
                                        "in class '$clazz' should be List<String>, but it is List<${paramValue.javaClass}>"
                        )
                    }

                    (translated as MutableList<String>).add(localizeValue(context, parameter, method, value))
                }
            } else if (paramValue is String) {
                translated = localizeValue(context, parameter, method, paramValue)
            } else {
                throw IllegalStateException(
                        "Parameter '${parameter.name}' in method '${method.name}' " +
                                "in class '$clazz' should be String or List<String>, but it is '${paramValue.javaClass}'"
                )
            }

            newParams[i] = translated
        }

        return method.invoke(obj, *newParams)
    }

    private fun localizeValue(
            context: LocalizationContext,
            parameter: Parameter,
            method: Method,
            parameterValue: String
    ): String {
        val translated = context.localizeOrNull(localizationKey(parameterValue))
        return if (translated == null) {
            LOGGER.warn(
                    "Can not translate params '${parameter.name}' from method '${method.name} " +
                            "in class '$clazz' with value '$parameterValue'"
            )

            parameterValue
        } else {
            translated
        }
    }

    private fun isParameterShouldLocalized(parameter: Parameter): Boolean {
        val isItHaveRightType = String::class.java.isAssignableFrom(parameter.type)
                || Iterable::class.java.isAssignableFrom(parameter.type)
                && checkGenericTypes(parameter.parameterizedType, GENERIC_TYPE_STRING)

        return isItHaveRightType && parameter.isAnnotationPresent(Localized::class.java)
    }

    private fun checkGenericTypes(forCheck: Type, types: Array<out Class<*>>): Boolean {
        if (forCheck !is ParameterizedType) {
            return false
        }

        val generics = forCheck.actualTypeArguments
        if (generics.size != types.size) {
            return false
        }

        for (i in generics.indices) {
            if (generics[i] !is Class<*> || !types[i].isAssignableFrom(generics[i] as Class<*>)) {
                return false
            }
        }
        return true
    }

    companion object {
        private val GENERIC_TYPE_STRING = arrayOf(String::class.java)
    }
}