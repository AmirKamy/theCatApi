import java.util.Properties

plugins {
    alias(libs.plugins.linker.android.library)
    alias(libs.plugins.linker.android.hilt)
    id("kotlinx-serialization")
}

val localProperties = File(rootDir, "local.properties")
    .inputStream()
    .use {
        val props = Properties()
        props.load(it)
        props
    }

val apiKey: String = localProperties.getProperty("API_KEY")
    ?: error("API_KEY not found in local.properties")

android {
    namespace = "com.linker.core.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
    }
}

dependencies {

    api(projects.core.common)
    api(projects.core.model)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)

    implementation(libs.logging.interceptor)

}