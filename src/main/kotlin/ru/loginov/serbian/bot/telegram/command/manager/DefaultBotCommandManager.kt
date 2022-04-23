package ru.loginov.serbian.bot.telegram.command.manager

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.telegram.command.BotCommand

@Service
class DefaultBotCommandManager : BotCommandManager {

    private val commands: MutableList<BotCommand> = ArrayList()
    private val commandByName: MutableMap<String, BotCommand> = HashMap()

    @Autowired
    fun initCommands(commands: List<BotCommand>) {
        this.commands.addAll(commands)
        this.commands.sortBy { it.commandName }

        commands.forEach {
            if (it.commandName.isNotEmpty()) {
                if (!commandByName.containsKey(it.commandName.lowercase())) {
                    commandByName[it.commandName.lowercase()] = it
                } else {
                    LOGGER.error("Can not register two commands with one name. Command from class '${it.javaClass}' will not register")
                }
            } else {
                LOGGER.warn("Can not register command with empty name. Class: '${it.javaClass}'")
            }
        }
    }

    override fun getAllCommands(): List<BotCommand> = commands

    override fun getCommandByName(name: String): BotCommand? = commandByName[name.lowercase()]

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultBotCommandManager::class.java)
    }
}