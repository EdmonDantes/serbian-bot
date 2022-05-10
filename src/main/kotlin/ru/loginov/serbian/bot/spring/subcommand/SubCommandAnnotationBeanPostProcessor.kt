package ru.loginov.serbian.bot.spring.subcommand

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.PriorityOrdered
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.telegram.command.BotCommand
import ru.loginov.serbian.bot.telegram.command.impl.ComplexBotCommand
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

//TODO: Add creating command tree and print it to log (example: { root: { commandClass1: { commandClass2: null }}})
@Component
class SubCommandAnnotationBeanPostProcessor : BeanPostProcessor, PriorityOrdered {

    private val botCommandsNames = HashMap<KClass<*>, String>()
    private val botCommandsClasses = HashMap<String, KClass<*>>()
    private val subCommands = HashMap<KClass<*>, MutableMap<String, String>>()
    private val subCommandsParent = HashMap<String, MutableList<KClass<*>>>()

    private val botCommands = HashMap<String, Any>()

    private val property: KMutableProperty<*> = ComplexBotCommand::class.memberProperties
            .find { it.name == "_subCommands" && it is KMutableProperty<*> } as KMutableProperty<*>?
            ?: error("Can not find property '_subCommands' in class 'ComplexBotCommand'")

    init {
        try {
            property.isAccessible = true
            property.setter.isAccessible = true
        } catch (e: Exception) {
            error("Can not get access to property '_subCommands' in class 'ComplexBotCommand'")
        }
    }

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean is BotCommand) {
            botCommandsNames[bean::class] = beanName
            botCommandsClasses[beanName] = bean::class

            bean.javaClass.kotlin.annotations
                    .filter { it is SubCommand }
                    .map { it as SubCommand }
                    .forEach { annotation ->
                        annotation.parents.forEach { parent ->
                            if (parent != bean::class) {
                                val oldBeanName = subCommands.computeIfAbsent(parent) { HashMap() }.put(
                                        bean.commandName,
                                        beanName
                                )
                                if (oldBeanName != null) {
                                    LOGGER.warn(
                                            "Command from class '$parent' have subcommands conflict " +
                                                    "for name '${bean.commandName}'. " +
                                                    "Conflicted beans: ['$beanName', '$oldBeanName']"
                                    )
                                }
                                subCommandsParent.computeIfAbsent(beanName) { ArrayList() }.add(parent)
                            }
                        }
                    }
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val kotlinClass = botCommandsClasses[beanName] ?: return bean

        botCommands[beanName] = bean

        processBotCommand(bean, kotlinClass)
        val parentsClasses = subCommandsParent[beanName]
        if (!parentsClasses.isNullOrEmpty()) {
            parentsClasses.mapNotNull {
                botCommandsNames[it]?.let { parentName ->
                    botCommands[parentName]?.let { bean ->
                        bean to it
                    }
                }
            }.forEach {
                processBotCommand(it.first, it.second)
            }
        }

        return bean
    }

    private fun processBotCommand(bean: Any, kotlinClass: KClass<*>): Boolean {
        if (bean is ComplexBotCommand) {
            val currentSubCommands = subCommands[kotlinClass]

            if (!currentSubCommands.isNullOrEmpty() && currentSubCommands.values.all { botCommands.containsKey(it) }) {
                //TODO: May be we should one set new map and save in this BPP
                property.setter.call(bean, currentSubCommands.mapValues { botCommands[it.value] })
                return true
            }
        }
        return false
    }

    override fun getOrder(): Int = 0

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandAnnotationBeanPostProcessor::class.java)
    }
}