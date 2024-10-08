plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.androidweather"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.androidweather"
        minSdk = 29
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(files("libs\\BaiduLBS_Android.jar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}