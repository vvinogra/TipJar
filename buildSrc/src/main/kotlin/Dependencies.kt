import org.gradle.api.artifacts.dsl.DependencyHandler

object Dependencies {

    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
        const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val material = "com.google.android.material:material:${Versions.material}"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.2.1"

        const val activity = "androidx.activity:activity-ktx:${Versions.activityKtx}"
        const val fragment = "androidx.fragment:fragment-ktx:${Versions.fragmentKtx}"
        const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
        const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"

        const val exifInterface = "androidx.exifinterface:exifinterface:${Versions.exifInterface}"
    }

    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    object Test {
        const val jUnit = "junit:junit:${Versions.jUnit}"
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutinesTest}"
        const val mockitoInline = "org.mockito:mockito-inline:${Versions.mockito}"
        const val mockitoCore = "org.mockito:mockito-core:${Versions.mockito}"
        const val mockitoKotlin = "org.mockito.kotlin:mockito-kotlin:${Versions.mockitoKotlin}"
    }

    object RoomDatabase {
        const val roomRuntime = "androidx.room:room-runtime:${Versions.roomVersion}"
        const val roomKtx = "androidx.room:room-ktx:${Versions.roomVersion}"
        const val roomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"
    }

    object DI {
        const val hilt = "com.google.dagger:hilt-android:${Versions.hilt}"
        const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
    }

    object Navigation {
        const val fragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
        const val uiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    }

    object Glide {
        const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
        const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
    }

    val navigationLibraries = arrayListOf<String>().apply {
        add(Navigation.fragment)
        add(Navigation.uiKtx)
    }

    val appUiLibraries = arrayListOf<String>().apply {
        add(AndroidX.appCompat)
        add(AndroidX.constraintLayout)
        add(AndroidX.coreKtx)
        add(AndroidX.material)
        add(AndroidX.recyclerView)
    }

    val ktxLibraries = arrayListOf<String>().apply {
        add(AndroidX.activity)
        add(AndroidX.fragment)
        add(AndroidX.lifecycle)
        add(AndroidX.livedata)
        add(AndroidX.viewModel)
    }

    val testLibraries = arrayListOf<String>().apply {
        add(Test.jUnit)
        add(Test.coroutinesTest)
        add(Test.mockitoCore)
        add(Test.mockitoInline)
        add(Test.mockitoKotlin)
    }
}

// Util functions for adding the different type dependencies from build.gradle file
fun DependencyHandler.kapt(list: List<String>) {
    list.forEach { dependency ->
        add("kapt", dependency)
    }
}

fun DependencyHandler.implementation(list: List<String>) {
    list.forEach { dependency ->
        add("implementation", dependency)
    }
}

fun DependencyHandler.androidTestImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("androidTestImplementation", dependency)
    }
}

fun DependencyHandler.testImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("testImplementation", dependency)
    }
}