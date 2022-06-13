plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":shared:database"))
    implementation(project(":shared:sharedpref"))

    //Hilt
    implementation(Dependencies.DI.hilt)
    kapt(Dependencies.DI.hiltCompiler)

    implementation(Dependencies.AndroidX.coreKtx)
    implementation(Dependencies.AndroidX.exifInterface)

    implementation(Dependencies.coroutines)
}