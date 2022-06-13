plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    defaultConfig {
        applicationId = Config.applicationId
        versionCode = Config.versionCode
        versionName = Config.versionName
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
    implementation(project(":features:core"))
    implementation(project(":features:navigation"))

    implementation(Dependencies.appUiLibraries)
    implementation(Dependencies.ktxLibraries)
    implementation(Dependencies.navigationLibraries)

    //Hilt
    implementation(Dependencies.DI.hilt)
    kapt(Dependencies.DI.hiltCompiler)
}