import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

val publishVersion = System.getenv("BITRISE_GIT_TAG")
    ?: localProperties["version"] as String?
    ?: "0.1.0-SNAPSHOT"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
    id("maven-publish")
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Transactions"
            isStatic = true
        }
    }

    androidLibrary {
        namespace = "dev.finio.transactions"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.android.driver)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
    }
}

sqldelight {
    databases {
        create("FinioTransactionsDatabase") {
            packageName.set("dev.finio.transactions.db")
        }
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            groupId = "dev.finio"
            version = publishVersion
            artifactId = when (name) {
                "android" -> "transaction-android"
                "iosArm64" -> "transaction-iosarm64"
                "iosSimulatorArm64" -> "transaction-iossimulatorarm64"
                "kotlinMultiplatform" -> "transaction-kmp"
                else -> "transaction-$name"
            }

            pom {
                name.set("Finio Transaction")
                description.set("Finios transaction KMP module")
                url.set("https://github.com/dgbarreto/finio-transaction")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licences/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("dgbarreto")
                        name.set("Danilo Barreto")
                        email.set("dgbarreto@gmail.com")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/dgbarreto/finio-transaction")
            credentials {
                username = localProperties["github.actor"] as String?
                    ?: System.getenv("GITHUB_ACTOR") ?: ""
                password = localProperties["github.token"] as String?
                    ?: System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
}