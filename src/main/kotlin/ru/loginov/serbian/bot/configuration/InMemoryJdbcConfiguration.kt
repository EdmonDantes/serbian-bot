package ru.loginov.serbian.bot.configuration

import org.h2.tools.Server
import org.hibernate.cfg.AvailableSettings
import org.hibernate.jpa.HibernatePersistenceProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.Properties
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource


@Configuration
@ConditionalOnProperty(name = ["bot.database.jdbc.in.memory.enabled"])
@EnableJpaRepositories(basePackages = ["ru.loginov.serbian.bot.data.repository.*"])
@EnableTransactionManagement
@ComponentScan(basePackages = ["ru.loginov.serbian.bot.data.*"])
@EntityScan(basePackages = ["ru.loginov.serbian.bot.data.*"])
@Import(InitSearchServices::class)
class InMemoryJdbcConfiguration {

    @Value("\${bot.database.jdbc.mixed.mode.enabled:false}")
    private var startInMixedMode: Boolean = false
    private var h2Server: Server? = null


    @PostConstruct
    fun init() {
        if (startInMixedMode) {
            try {
                h2Server = Server.createTcpServer("-tcpAllowOthers", "-ifNotExists")
                h2Server!!.start()
                LOGGER.info("Start H2 TCP server on url '${h2Server!!.url}' and port '${h2Server!!.port}'")
            } catch (e: Exception) {
                h2Server = null
                LOGGER.error("Can not start H2 TCP server", e)
            }
        }
    }

    @Bean
    fun dataSource(): DataSource = DriverManagerDataSource().apply {
        setDriverClassName("org.h2.Driver")
        url = if (startInMixedMode) {
            "jdbc:h2:${h2Server!!.url}/~/helloworld;DB_CLOSE_DELAY=-1"
        } else {
            "jdbc:h2:mem:helloworld;DB_CLOSE_DELAY=-1"
        }
        username = "sa"
        password = "sa"
    }

    @Bean
    fun transactionManager(): JpaTransactionManager = JpaTransactionManager().apply {
        entityManagerFactory = entityManagerFactory()
    }

    @Bean
    fun entityManagerFactoryBean(): LocalContainerEntityManagerFactoryBean = LocalContainerEntityManagerFactoryBean().apply {
        jpaVendorAdapter = HibernateJpaVendorAdapter().apply { setShowSql(true) }
        dataSource = dataSource()
        setPersistenceProviderClass(HibernatePersistenceProvider::class.java)
        setPackagesToScan(
                "ru.loginov.serbian.bot.data.*"
        )
        setJpaProperties(Properties().apply {
            put(AvailableSettings.HBM2DDL_AUTO, "create-drop")
            put(AvailableSettings.DIALECT, "org.hibernate.dialect.H2Dialect")
            put("hibernate.search.backend.directory.root", "./search_index") //Need for hibernate search + lucene
        })
    }

    @Bean
    @Primary
    fun entityManagerFactory(): EntityManagerFactory = entityManagerFactoryBean().`object`
            ?: error("Can not create bean 'entityManagerFactory' in class ${InMemoryJdbcConfiguration::class}")

    @PreDestroy
    fun preDestroy() {
        try {
            h2Server?.stop()
        } catch (e: Exception) {
            LOGGER.warn("Can not stor H2 TCP server", e)
        }
    }


    companion object {
        private val LOGGER = LoggerFactory.getLogger(InMemoryJdbcConfiguration::class.java)
    }
}