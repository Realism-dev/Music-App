plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
//    alias(libs.plugins.room)// Подключаем плагин Room в нужном модуле
}

android {
    namespace = "dev.realism.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    //Room
    implementation (libs.room.runtime)
    implementation (libs.room.ktx)
    ksp(libs.room.compiler ) // Для работы с аннотациями Room
    testImplementation (libs.room.testing)

    //Dagger
    implementation (libs.dagger)
    ksp(libs.dagger.compiler)  // Для компиляции аннотаций Dagger

    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}