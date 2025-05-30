package com.example.thecatapiexample.build_logic.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {

    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            implementation(platform(bom))
            androidTestImplementation(platform(bom))
            implementation(libs.findLibrary("androidx.compose.material3").get())
            debugImplementation(libs.findLibrary("androidx-compose-ui-tooling").get())
            implementation(libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            implementation(libs.findLibrary("androidx.compose.foundation").get())
            implementation(libs.findLibrary("androidx.compose.foundation.layout").get())
            implementation(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
            implementation(libs.findLibrary("androidx.compose.ui.ui").get())
        }
    }
}
