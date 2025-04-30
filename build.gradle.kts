plugins {
    kotlin("jvm") version "2.1.10"
    jacoco
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // koin
    implementation("io.insert-koin:koin-core:4.0.3")

    // kotlin date time
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")


    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation(kotlin("test"))


    // kotest, assertion
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")

    // google truth
    testImplementation("com.google.truth:truth:1.4.2")

    // mockk
    testImplementation("io.mockk:mockk:1.14.0")

    // junit params
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")

    //Data Frame
    implementation("org.jetbrains.kotlinx:dataframe:0.15.0")
    implementation("org.slf4j:slf4j-nop:2.0.9")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}

tasks.jacocoTestReport {
    reports {
        csv.required.set(true)  // Enable CSV reports for additional processing if needed
        xml.required.set(true)  // Required for coverage-diff to work
        html.required.set(true) // Human-readable reports
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)

    classDirectories.setFrom(
        fileTree("build/classes/kotlin/main") {
            exclude("**/generated/**")
        }
    )
    sourceDirectories.setFrom(files("src/main/kotlin"))
    executionData.setFrom(fileTree(buildDir).include("jacoco/test.exec"))

    violationRules {
        rule {
            limit {
                minimum = "1".toBigDecimal() // 100% coverage requirement
            }
        }
        rule {
            element = "CLASS"
            includes = listOf("org.example.*") // Adjust package name as needed

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "1".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "1".toBigDecimal()
            }
            limit {
                counter = "METHOD"
                value = "COVEREDRATIO"
                minimum = "1".toBigDecimal()
            }
        }
    }
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(tasks.test)

    classDirectories.setFrom(
        fileTree("build/classes/kotlin/main") {
            exclude("**/generated/**")
        }
    )
    sourceDirectories.setFrom(files("src/main/kotlin"))
    executionData.setFrom(fileTree(buildDir).include("jacoco/test.exec"))
}