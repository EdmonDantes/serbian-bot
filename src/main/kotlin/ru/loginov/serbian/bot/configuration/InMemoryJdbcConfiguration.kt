package ru.loginov.serbian.bot.configuration

import org.hibernate.cfg.AvailableSettings
import org.hibernate.jpa.HibernatePersistenceProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.*
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.Properties
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

    @Bean
    fun dataSource(): DataSource = DriverManagerDataSource().apply {
        setDriverClassName("org.h2.Driver")
        url = "jdbc:h2:mem:helloworld;DB_CLOSE_DELAY=-1"
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


}