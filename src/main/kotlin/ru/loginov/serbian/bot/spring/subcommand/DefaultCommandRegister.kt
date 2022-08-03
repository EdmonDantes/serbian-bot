package ru.loginov.serbian.bot.spring.subcommand

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.telegram.command.BotCommand
import ru.loginov.serbian.bot.telegram.command.base.ComplexBotCommand
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@Service
class DefaultCommandRegister : CommandRegister {

    private val subCommands = HashMap<KClass<*>, MutableMap<String, BotCommand>>()
    private val subCommandsByBeanNames = HashMap<KClass<*>, MutableMap<String, String>>()
    private val botCommands = HashMap<String, BotCommand>()
    private val subCommandParents = HashMap<String, MutableList<KClass<*>>>()

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

    override fun registerBean(bean: BotCommand, beanName: String) {
        subCommandsByBeanNames.computeIfAbsent(bean::class) { HashMap() }
        botCommands[beanName] = bean
    }

    override fun registerNewSubCommand(bean: BotCommand, beanName: String, parent: KClass<*>) {
        if (parent == bean::class) {
            return
        }

        val oldBeanName = subCommandsByBeanNames.computeIfAbsent(parent) { HashMap() }.put(
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
        subCommandParents.computeIfAbsent(beanName) { ArrayList() }.add(parent)
    }

    override fun injectBean(beanName: String, bean: BotCommand) {
        subCommandParents[beanName]?.forEach { parent ->
            subCommands.computeIfAbsent(parent) { HashMap() }[bean.commandName] = bean
        }
    }

    override fun injectSubCommands(beanName: String) {
        val bean = botCommands[beanName]
        if (bean !is ComplexBotCommand) {
            return
        }

        val subCommandsMap = subCommands.computeIfAbsent(bean::class) { HashMap() }
        property.setter.call(bean, subCommandsMap)
    }

    override fun toString(): String {
        val root = ObjectNode(MAPPER.nodeFactory)
        botCommands.filter { !subCommandParents.containsKey(it.key) }.forEach { (_, value) ->
            addToNode(root, value.commandName, value::class)
        }

        return MAPPER.writeValueAsString(root)
    }

    private fun addToNode(objectNode: ObjectNode, commandName: String, clazz: KClass<*>?) {
        val innerNode = objectNode.putObject(commandName)
        innerNode.put("__class", clazz?.qualifiedName ?: "unknown")
        subCommandsByBeanNames[clazz]?.also {
            if (it.isNotEmpty()) {
                it.forEach { (commandName, beanName) ->
                    addToNode(innerNode, commandName, botCommands[beanName]?.let { it::class })
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultCommandRegister::class.java)
        private val MAPPER = ObjectMapper()
    }
}