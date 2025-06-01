pluginManagement {
  repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
  }
}

rootProject.name = "Comics Reader"
include("app")
include("capacitor-cordova-android-plugins")
project(":capacitor-cordova-android-plugins").projectDir = File("./capacitor-cordova-android-plugins/")

apply(from = "capacitor.settings.gradle")