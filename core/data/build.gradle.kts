plugins {
    alias(libs.plugins.linker.android.library)
    alias(libs.plugins.linker.android.hilt)
}

android {
    namespace = "com.example.core.data"
}

dependencies {
    api(projects.core.common)
    api(projects.core.database)
    api(projects.core.network)


    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.retrofit.core)

}