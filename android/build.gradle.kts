// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version("2.1.20") apply(false)
    id("com.android.application") version("8.10.1") apply(false)
    id("org.jetbrains.kotlin.android") version("2.1.21") apply(false)
    id("com.google.devtools.ksp") version("2.1.21-2.0.1") apply(false)
    id("androidx.room") version("2.7.1") apply(false)
}

tasks.register("clean", Delete::class) {
    delete { rootProject.buildDir }
}
