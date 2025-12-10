plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.room")
    id("com.google.devtools.ksp")
}

// 配置 Room 插件的 DSL（核心修复点）
room {
    // 指定 schema 文件的存储目录，路径可自定义
    schemaDirectory("$projectDir/src/main/schemas")
}

android {
    namespace = "com.swu.databaseroomtest"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.swu.databaseroomtest"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room 数据库依赖
    implementation("androidx.room:room-runtime:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")
    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.8.4")
    
    // Lifecycle 支持（用于 lifecycleScope）
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
}