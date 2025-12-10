import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
}

android {
    namespace = "com.example.explorelocal"
    compileSdk = 36

//    Already Deprecated
//    val key: String = com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(project.rootDir)
//        .getProperty("supabaseKey")
//    val url: String = com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(project.rootDir)
//        .getProperty("supabaseUrl")

    val localProps = Properties()
    localProps.load(rootProject.file("local.properties").inputStream())

    val key = localProps.getProperty("supabaseKey")
    val url = localProps.getProperty("supabaseUrl")

    defaultConfig {
        applicationId = "com.example.explorelocal"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String","supabaseKey","\"$key\"")
        buildConfigField("String","supabaseUrl","\"$url\"")
    }


    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(platform("io.github.jan-tennert.supabase:bom:2.6.0"))
    implementation("io.ktor:ktor-client-cio:2.3.4")
    implementation("io.github.jan-tennert.supabase:gotrue-kt")
    implementation("io.github.jan-tennert.supabase:storage-kt") // Storage
    implementation("io.github.jan-tennert.supabase:realtime-kt") // Realtime
    implementation("io.github.jan-tennert.supabase:postgrest-kt")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    implementation("androidx.compose.runtime:runtime:1.9.5")
    implementation("androidx.compose.runtime:runtime-livedata:1.9.5")
    implementation("androidx.compose.material:material-icons-extended-android:1.7.8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

}