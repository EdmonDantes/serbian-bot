version = "0.0.1-SNAPSHOT"

dependencies {
    api(project(":simple-permissions-core"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")

    implementation("org.springframework.boot:spring-boot-autoconfigure:2.7.0")
    implementation("jakarta.persistence:jakarta.persistence-api:2.2.3")
    implementation("org.springframework.data:spring-data-jpa:2.7.0")
    implementation("org.springframework:spring-context:5.3.20")
}