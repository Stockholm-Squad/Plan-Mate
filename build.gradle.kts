plugins {
    kotlin("jvm") version "2.1.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
    implementation("io.insert-koin:koin-core:4.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("io.mockk:mockk:1.14.0")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}