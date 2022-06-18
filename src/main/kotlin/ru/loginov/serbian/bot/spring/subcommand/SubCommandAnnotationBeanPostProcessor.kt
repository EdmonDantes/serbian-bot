package ru.loginov.serbian.bot.spring.subcommand

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.subcommand.annotation.SubCommand
import ru.loginov.serbian.bot.telegram.command.BotCommand

@Component
class SubCommandAnnotationBeanPostProcessor : BeanPostProcessor, ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private lateinit var register: CommandRegister

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean is BotCommand) {
            register.registerBean(bean, beanName)

            bean.javaClass.kotlin.annotations
                    .filter { it is SubCommand }
                    .map { it as SubCommand }
                    .forEach { annotation ->
                        annotation.parents.forEach { parent ->
                            register.registerNewSubCommand(bean, beanName, parent)
                        }
                    }
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        if (bean is BotCommand) {
            register.injectSubCommands(beanName)
            register.injectBean(beanName, bean)
        }
        return bean
    }

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        LOGGER.debug("Commands hierarchy: '$register'")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubCommandAnnotationBeanPostProcessor::class.java)
    }
}