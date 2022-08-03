package ru.loginov.serbian.bot.configuration

import org.h2.tools.Server
import org.hibernate.cfg.AvailableSettings
import org.hibernate.jpa.HibernatePersistenceProvider
import org.hibernate.tool.schema.Action
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigurationPackages
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.domain.EntityScanPackages
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.util.StringUtils
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource


@Configuration
@ConditionalOnProperty(name = ["bot.database.jdbc.in.memory.enabled"], havingValue = "true")
@EnableTransactionManagement
@EnableJpaRepositories
@ComponentScan(basePackages = ["ru.loginov.serbian.bot.data.*", "ru.loginov.serbian.bot.data.repository.*"])
@EntityScan(basePackages = ["ru.loginov.serbian.bot.data.*"])
@Import(JpaRepositoriesAutoConfiguration::class)
class InMemoryJdbcConfiguration(
        @Value("\${bot.database.jdbc.mixed.mode.enabled:false}") private val startInMixedMode: Boolean,
        @Value("\${bot.database.jdbc.save.data.enabled:false}") private val shouldSaveData: Boolean,
        @Value("\${bot.database.jdbc.database.name:serbian_bot}") private val databaseName: String,
) {

    private var h2Server: Server? = null

    @PostConstruct
    fun init() {
        if (startInMixedMode) {
            try {
                h2Server = Server.createTcpServer("-tcpAllowOthers", "-ifNotExists", "-baseDir", "./db/")
                h2Server!!.start()
                LOGGER.info("Start H2 TCP server on url '${h2Server!!.url}' and port '${h2Server!!.port}'")
            } catch (e: Exception) {
                h2Server = null
                LOGGER.error("Can not start H2 TCP server in mixed mode", e)
            }
        }
    }

//    @Bean
//    @Primary
//    fun dataSource(): DataSource = DriverManagerDataSource().apply {
//        setDriverClassName("org.h2.Driver")
//        val serverUrl = h2Server?.url?.let { "$it/" } ?: "./db:"
//        url = "jdbc:h2:$serverUrl$databaseName;DB_CLOSE_DELAY=-1"
//        username = "sa"
//        password = "sa"
//    }

    @Bean
    fun transactionManager(entityManagerFactory: EntityManagerFactory): JpaTransactionManager = JpaTransactionManager().apply {
        this.entityManagerFactory = entityManagerFactory
    }

    @Bean
    fun entityManagerFactoryBean(
            dataSource: DataSource,
            properties: JpaProperties?,
            beanFactory: BeanFactory
    ): LocalContainerEntityManagerFactoryBean = LocalContainerEntityManagerFactoryBean().apply {
        jpaVendorAdapter = HibernateJpaVendorAdapter().apply { setShowSql(true) }
        this.dataSource = dataSource
        setPersistenceProviderClass(HibernatePersistenceProvider::class.java)
        setPackagesToScan(*getPackagesToScan(beanFactory))

        val additionalProperties = mapOf(
                AvailableSettings.DIALECT to "org.hibernate.dialect.H2Dialect",
                AvailableSettings.HBM2DDL_AUTO to (if (shouldSaveData) Action.UPDATE else Action.CREATE_DROP).externalHbm2ddlName,
                "hibernate.search.backend.directory.root" to "./search_index", //Need for hibernate search + lucene
                "hibernate.search.configuration_property_checking.strategy" to "ignore"
        )
        setJpaPropertyMap(properties?.properties?.plus(additionalProperties) ?: additionalProperties)
    }

    @Bean
    @Primary
    fun entityManagerFactory(entityManagerFactoryBean: LocalContainerEntityManagerFactoryBean): EntityManagerFactory =
            entityManagerFactoryBean.`object`
                    ?: error("Can not create bean 'entityManagerFactory' in class ${InMemoryJdbcConfiguration::class}")

    @PreDestroy
    fun preDestroy() {
        try {
            h2Server?.stop()
        } catch (e: Exception) {
            LOGGER.warn("Can not stop H2 TCP server", e)
        }
    }

    protected fun getPackagesToScan(beanFactory: BeanFactory): Array<String> {
        var packages = EntityScanPackages.get(beanFactory).packageNames
        if (packages.isEmpty() && AutoConfigurationPackages.has(beanFactory)) {
            packages = AutoConfigurationPackages.get(beanFactory)
        }
        return StringUtils.toStringArray(packages)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(InMemoryJdbcConfiguration::class.java)
    }
}