plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
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

    viewBinding {
        android.buildFeatures.viewBinding = true
    }
}

dependencies {
    implementation(project(":shared:ui"))
    implementation(project(":shared:data"))
    implementation(project(":shared:viewbindingdelegate"))

    implementation(Dependencies.Glide.glide)
    kapt(Dependencies.Glide.glideCompiler)

    implementation(Dependencies.appUiLibraries)
    implementation(Dependencies.ktxLibraries)

    //Hilt
    implementation(Dependencies.DI.hilt)
    kapt(Dependencies.DI.hiltCompiler)

    testImplementation(Dependencies.testLibraries)
}