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
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.1")

    api("io.ktor:ktor-client-core:1.6.7")
    api("io.ktor:ktor-client-cio:1.6.7")
    api("io.ktor:ktor-client-okhttp:1.6.7")

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