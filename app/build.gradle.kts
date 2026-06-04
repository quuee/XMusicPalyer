plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)

}

android {
    namespace = "cn.x"
    compileSdk = 36
    defaultConfig {
        applicationId = "cn.x"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true // 启用代码混淆、压缩
            isShrinkResources = true // 移除无用的资源
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            ndk.abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }

}

dependencies {

// AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)

    // Compose BOM 管理版本
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // Compose 预览和调试
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    // Navigation3
    implementation(libs.bundles.navigation3)

    // Koin DI
    implementation(libs.bundles.koin)

    // Network
    implementation(libs.bundles.network)


    // Room
    implementation(libs.bundles.room)
    ksp(libs.room.compiler) // 强依赖 KSP 插件

    // Coroutines
    implementation(libs.bundles.coroutines)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Coil 图片加载
    implementation(libs.coil.compose)

    // toaster
    implementation(libs.toaster)

    implementation(libs.mmkv)

    // Permissions
    implementation(libs.accompanist.permissions)


    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Media3 (ExoPlayer) - Google官方推荐的媒体播放库
    implementation("androidx.media3:media3-exoplayer:1.7.1")
    implementation("androidx.media3:media3-ui:1.7.1") // 包含MediaControlView等UI组件
    implementation("androidx.media3:media3-session:1.7.1") // 用于MediaSession和后台播放

    implementation("net.jthink:jaudiotagger:3.0.1")

}

// 关键配置：告诉 KSP 把 schema 输出到 src/main/schemas 目录
//ksp {
//    arg("room.schemaLocation", "$projectDir/src/main/schemas")
//}