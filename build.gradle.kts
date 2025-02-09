val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val nettyVersion: String by project
val jacocoVersion: String by project

plugins {
    application
    kotlin("jvm") version "2.0.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
    jacoco
}

group = "com.templatetasks.kotlin.ktor"
version = "0.0.1"
application {
    mainClass.set("com.templatetasks.kotlin.ktor.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    api(platform("io.netty:netty-bom:$nettyVersion"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("commons-codec:commons-codec:1.17.1")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

jacoco {
    toolVersion = jacocoVersion // Use the defined version
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Ensure tests run first

    reports {
        xml.required.set(true)
        csv.required.set(false) // Disable CSV report
        html.required.set(true)
//        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml")) // Generate HTML report
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)

    violationRules {
        rule {
            limit {
                minimum = "0.5".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
