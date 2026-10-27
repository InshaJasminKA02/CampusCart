plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.campuscart.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.campuscart.app"
        minSdk = 24
        targetSdk = 35
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
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // Note: Firebase BoM (firebase-firestore, firebase-auth) are prepared in libs.versions.toml
    // and ready to be connected once google-services.json and active network are configured.

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
