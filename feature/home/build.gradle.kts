import com.example.thecatapiexample.build_logic.convention.implementation

plugins {
    alias(libs.plugins.linker.android.feature)
    alias(libs.plugins.linker.android.library.compose)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.linker.feature.home"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.mpandroidchart)
    implementation(libs.androidx.compose.material3)
//    implementation(libs.accompanist)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}