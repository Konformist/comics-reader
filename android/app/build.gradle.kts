plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.android")
    id("androidx.room")
}

kotlin {
    jvmToolchain(21)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.konformist.comicsreader"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.konformist.comicsreader"
        minSdk = 34
        targetSdk = 35
        versionCode = 17
        versionName = "1.4.11"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        androidResources {
            localeFilters.addAll(listOf("en", "ru"))
            // Files and dirs to omit from the packaged assets dir, modified to accommodate modern web apps.
            // Default: https://android.googlesource.com/platform/frameworks/base/+/282e181b58cf72b6ca770dc7ca5f91f135444502/tools/aapt/AaptAssets.cpp#61
            ignoreAssetsPattern = "!.svn:!.git:!.ds_store:!*.scc:.*:!CVS:!thumbs.db:!picasa.ini:!*~"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

repositories {
    mavenCentral()
    google()
    flatDir {
        dirs("../capacitor-cordova-android-plugins/src/main/libs", "libs")
    }
}

dependencies {
    val mockito = "5.18.0"
    val androidJunit = "1.2.1"
    val room = "2.7.1"

    implementation("org.jsoup:jsoup:1.17.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.apache.commons:commons-compress:1.27.1")
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    implementation("androidx.room:room-runtime:$room")
    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room")
    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.3.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(project(":capacitor-android"))
    implementation(project(":capacitor-cordova-android-plugins"))

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:$mockito")
    androidTestImplementation("org.mockito:mockito-android:$mockito")
    androidTestImplementation("androidx.test.ext:junit:$androidJunit")
    androidTestImplementation("androidx.test.ext:junit-ktx:$androidJunit")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

apply(from = "capacitor.build.gradle")
