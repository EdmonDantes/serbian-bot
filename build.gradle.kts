import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.6"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
}

group = "ru.loginov"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	//implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("mysql:mysql-connector-java")
	implementation("com.h2database:h2")

	implementation(project(":kotlin-telegram-api"))

	implementation("org.hibernate.search:hibernate-search-mapper-orm:6.1.4.Final")
//	implementation("org.hibernate:hibernate-search-orm:5.11.10.Final")
//	implementation("org.hibernate.search:hibernate-search-backend-elasticsearch:6.1.4.Final")
	implementation("org.hibernate.search:hibernate-search-backend-lucene:6.1.4.Final")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
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
