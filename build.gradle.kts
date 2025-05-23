plugins {
    kotlin("jvm") version "2.1.10"
    jacoco
    kotlin("plugin.serialization") version "2.1.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    addKoinDependencies()
    addKotlinXDependencies()
    addMongoDependencies()
    addTestDependencies()
    //logger
    implementation("org.slf4j:slf4j-nop:2.0.9")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
}

fun addTestDependencies() {
    dependencies {
        testImplementation("org.jetbrains.kotlin:kotlin-test")
        testImplementation(kotlin("test"))

        testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
        testImplementation("io.kotest:kotest-assertions-core:5.7.2")

        testImplementation("com.google.truth:truth:1.4.2")

        testImplementation("io.mockk:mockk:1.14.0")

        testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
        testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    }
}

fun addKotlinXDependencies() {
    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
        implementation("org.jetbrains.kotlinx:dataframe:0.15.0")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    }
}

fun addMongoDependencies() {
    dependencies {
        implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.1")
        implementation("org.litote.kmongo:kmongo-coroutine:4.10.0")
        implementation("org.litote.kmongo:kmongo-serialization:4.10.0")
    }
}

fun addKoinDependencies() {
    dependencies {
        implementation("io.insert-koin:koin-core:4.0.3")
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

val includedPackages = listOf(
    "src/main/kotlin/data/**",
    "src/main/kotlin/logic/**",
    "src/main/kotlin/ui/**"
)

val excludedPackages = listOf(
    "src/main/kotlin/di/**",
    "src/main/kotlin/input_output/**",
    "src/main/kotlin/utils/**"
)

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    classDirectories.setFrom(
        fileTree(layout.buildDirectory.dir("classes/kotlin/main")) {
            include(includedPackages)
            exclude(excludedPackages)
        }
    )
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    violationRules {
        rule {
            limit {
                minimum = "0.8".toBigDecimal()
            }
        }
    }

    classDirectories.setFrom(
        fileTree(layout.buildDirectory.dir("classes/kotlin/main")) {
            include(includedPackages)
            exclude(excludedPackages)
        }
    )
}

