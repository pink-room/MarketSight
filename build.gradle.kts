// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.jsonSerialization) apply false
    alias(libs.plugins.ksp) apply false
}