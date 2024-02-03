plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.travel_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.travel_app"
        minSdk = 24
        targetSdk = 33
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10") // 변경된 부분
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1") // 변경된 부분
    implementation("androidx.activity:activity-compose:1.4.0") // 변경된 부분
    implementation("androidx.compose.ui:ui:1.1.0")
    implementation("androidx.compose.ui:ui-graphics:1.1.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.1.0")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.gms:play-services-base:18.3.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.1.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.1.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.1.0")

}
