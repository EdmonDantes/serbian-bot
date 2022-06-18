import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
}

group = "ru.loginov"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":simple-permissions-core"))
    api("org.springframework.boot:spring-boot-autoconfigure:2.7.0")
    api("jakarta.persistence:jakarta.persistence-api:2.2.3")
    api("org.springframework.data:spring-data-jpa:2.7.0")
    api("org.springframework:spring-context:5.3.20")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}