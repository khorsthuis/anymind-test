import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.Logging

plugins {
    id("application")
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    id("nu.studer.jooq") version "8.1"
//    id("com.palantir.docker") version "0.34.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
}

group = "com.AnyMind"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

application {
    mainClass.set("com/AnyMind/api/ApiApplicationKt")
}

tasks.jar {
    manifest {
        archiveFileName.set("api.jar")
        attributes.set("Main-Class", "com.AnyMind.api.ApiApplicationKt")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.postgresql:postgresql")
    implementation("com.graphql-java:graphql-java-extended-scalars:20.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework:spring-webflux")
    testImplementation("org.springframework.graphql:spring-graphql-test")
    testImplementation("org.mockito:mockito-core:3.+")
    testImplementation("org.mockito.kotlin", "mockito-kotlin", "4.0.0")
    // jooq generator dependency
    jooqGenerator("org.postgresql:postgresql:42.5.1")
}

// configuration for generating jooq objects
jooq {
    version.set("3.17.6") // default (can be omitted)
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS) // default (can be omitted)

    configurations {
        create("main") { // name of the jOOQ configuration
            generateSchemaSourceOnCompilation.set(true) // default (can be omitted)

            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5432/postgres"
                    user = "postgres"
                    password = "123"
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                    }
                    generate.apply {
                        isGeneratedAnnotation = true
                        isRelations = true
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "com.AnyMind.api"
                        directory = "src/main/generated/java" // default (can be omitted)
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

// generateJooq gradle task
tasks.named<nu.studer.gradle.jooq.JooqGenerate>("generateJooq") { allInputsDeclared.set(true) }

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
