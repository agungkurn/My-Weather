import java.io.FileInputStream
import java.util.Properties

plugins {
    id(libs.plugins.common.library.get().pluginId)
}

fun getLocalProperty(propertyName: String): String? {
    val localProperties = Properties()
    val localPropertiesFile = File("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
    }
    return localProperties.getProperty(propertyName)
}


android {
    namespace = "id.ak.myweather.data"

    defaultConfig {
        buildConfigField("String", "API_KEY", "${getLocalProperty("API_KEY")}")
        buildConfigField("String", "BASE_URL", "${getLocalProperty("BASE_URL")}")
        buildConfigField("String", "BASE_IMAGE_URL", "${getLocalProperty("BASE_IMAGE_URL")}")
    }
}

dependencies {
    implementation(projects.domain)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.no.op)
}