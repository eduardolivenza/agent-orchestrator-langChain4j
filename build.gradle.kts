plugins {
    java
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // LangChain4J core + OpenAI provider + AgenticScope
    implementation("dev.langchain4j:langchain4j:1.16.3")
    implementation("dev.langchain4j:langchain4j-open-ai:1.16.3")
    implementation("dev.langchain4j:langchain4j-agentic:1.16.3-beta26")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Required so LangChain4J can read @Tool method parameter names via reflection
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
