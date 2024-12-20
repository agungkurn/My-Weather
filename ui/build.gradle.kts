plugins {
    id(libs.plugins.common.library.get().pluginId)
    id(libs.plugins.kotlin.compose.get().pluginId)
}

android {
    namespace = "id.ak.myweather.ui"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(projects.domain)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.viewmodel)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.coil.compose)
    implementation(libs.lottie)

    implementation(libs.play.location)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}