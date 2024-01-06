plugins {
    java
}

group = "io.github.noobsystems"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    compileOnly("eu.cloudnetservice.cloudnet:bridge:4.0.0-RC9")
    compileOnly("eu.cloudnetservice.cloudnet:platform-inject-api:4.0.0-RC9")
    annotationProcessor("eu.cloudnetservice.cloudnet:platform-inject-processor:4.0.0-RC9")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}


tasks.compileJava {
    options.encoding = Charsets.UTF_8.name()
}