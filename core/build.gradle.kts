plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    macosX64("darwin") {
        binaries {
            executable {
                entryPoint = "io.github.thalesdev.kompakt.main"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.io)
                implementation(libs.kotlinx.coroutines)
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
    }
}