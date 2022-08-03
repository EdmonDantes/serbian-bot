import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("org.springframework.boot") version "2.6.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.palantir.docker") version "0.33.0"
    kotlin("jvm") version "1.6.21" apply false
    kotlin("plugin.spring") version "1.6.10"
}

allprojects {
    apply(plugin = "kotlin")

    group = "ru.loginov"
    java.sourceCompatibility = JavaVersion.VERSION_11

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
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
}

group = "ru.loginov"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
	// --------- Kotlin --------- \\
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")

	// --------- Spring ---------- \\
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")

	// ------- JSON processing --- \\
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// --------- Databases ------ \\
	implementation("mysql:mysql-connector-java")
	implementation("com.h2database:h2")

	// --------- Search --------- \\
	implementation("org.hibernate.search:hibernate-search-mapper-orm:6.2.0.Alpha1")
//	implementation("org.hibernate:hibernate-search-orm:5.11.10.Final")
	implementation("org.hibernate.search:hibernate-search-backend-elasticsearch:6.2.0.Alpha1")
	//implementation("org.hibernate.search:hibernate-search-backend-lucene:6.1.4.Final")

    // --------- Telegram ------- \\
    implementation(project(":kotlin-telegram-api"))

    // --------- Google ---------- \\
    implementation("com.google.maps:google-maps-services:2.0.0")

    // --------- Permissions ----- \\
    implementation(project(":simple-permissions-spring-integration"))

    // --------- Localization ---- \\
    implementation("io.github.edmondantes:simple-localization:0.1.0")

    // --------- Callbacks ------- \\
    implementation("io.github.edmondantes:simple-kotlin-callbacks:0.1.1")

    // --------- Logging --------- \\
    //implementation("org.apache.logging.log4j:log4j-to-slf4j:2.17.2")
    //implementation("org.apache.logging.log4j:log4j-core:2.17.2")

    // --------- TEST ------------ \\
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks {
	named<Copy>("dockerPrepare") {
		val distTarTask = findByName("distTar")
		if (distTarTask != null) {
			dependsOn(distTarTask)
		}
	}
}

application {
	mainClass.set("ru.loginov.serbian.bot.SerbianBotApplicationKt")
}
