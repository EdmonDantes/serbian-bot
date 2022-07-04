version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")

    api("io.ktor:ktor-client-core:1.6.7")
    api("io.ktor:ktor-client-cio:1.6.7")
    api("io.ktor:ktor-client-okhttp:1.6.7")

}