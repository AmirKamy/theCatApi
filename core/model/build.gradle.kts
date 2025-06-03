plugins {
    alias(libs.plugins.linker.android.library)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.linker.core.model"
}

dependencies {

}