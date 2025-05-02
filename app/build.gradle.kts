import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
}

// Загрузка .env файла
val envFile = file("../.env")
if (envFile.exists()) {
    val properties = Properties()
    properties.load(FileInputStream(envFile))
    properties.forEach { key, value ->
        println("Loading property: $key = $value")  // Вывод для отладки
        System.setProperty(key.toString(), value.toString())
    }
} else {
    println("No .env file found. Ensure it's in the correct path.")
}

android {
    namespace = "com.example.sheduleapp_v5"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sheduleapp_v5"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getProperty("KEYSTORE_PATH") ?: throw IllegalStateException("KEYSTORE_PATH is missing"))
            storePassword = System.getProperty("KEYSTORE_PASSWORD") ?: throw IllegalStateException("KEYSTORE_PASSWORD is missing")
            keyAlias = System.getProperty("KEY_ALIAS") ?: throw IllegalStateException("KEY_ALIAS is missing")
            keyPassword = System.getProperty("KEY_PASSWORD") ?: throw IllegalStateException("KEY_PASSWORD is missing")
            println("Signing with keystore: ${storeFile}, alias: $keyAlias")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            println("Release build signingConfig: ${signingConfig?.name}")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.ktx)

    implementation(libs.jetbrains.annotations)
    implementation(libs.work)
    implementation(libs.fuzzywuzzy)
    implementation(libs.markwon)
}
