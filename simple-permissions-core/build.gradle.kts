import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
}

group = "ru.loginov"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    api("org.slf4j:slf4j-api:1.7.36")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}