plugins{
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}


dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.gradle.tools)
    implementation(libs.gradle.api)

    implementation(libs.kotlin.stdlib)
    implementation(kotlin("gradle-plugin", libs.versions.kotlin.get()))
    implementation(libs.compose.compiler.plugin)

    implementation(libs.hilt.plugin)

    // NOTE: Do not place your application dependencies here; they belong
    // in the individual module build.gradle files
}