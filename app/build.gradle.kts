import java.util.Properties
import java.io.FileInputStream
import java.io.File

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.secrets)
}

// Загрузка .env файла
val envFile = file("../.env")
if (envFile.exists()) {
    val properties = Properties()
    properties.load(FileInputStream(envFile))
    properties.forEach { key, value ->
        println("Loading property: $key = $value")
        System.setProperty(key.toString(), value.toString())
    }
} else {
    println("No .env file found. Ensure it's in the correct path.")
}

// Чтение ключей из local.properties
val localProperties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.example.irkpo_management"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.irkpo_management"
        minSdk = 26
        targetSdk = 36
        versionCode = 11
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
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
//            signingConfig = signingConfigs.getByName("release")
//            println("Release build signingConfig: ${signingConfig?.name}")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

dependencies {
    // base
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    //Tests
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation (libs.mockito.inline)
    testImplementation(libs.robolectric)
    testImplementation (libs.core)
    testImplementation (libs.work.testing)
    testImplementation (libs.room.testing)
    testImplementation (libs.retrofit.mock)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Network
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.ktx)

    // GitHub
    implementation (libs.mpandroidchart)
    implementation (libs.itext7.core)

    // Excel
    implementation(libs.poi)
    implementation(libs.poi.ooxml)
    implementation(libs.jfreechart)

    // Other
    implementation(libs.jetbrains.annotations)
    implementation(libs.work)
    implementation(libs.fuzzywuzzy)
    implementation(libs.markwon)
}