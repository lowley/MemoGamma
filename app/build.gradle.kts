import com.google.protobuf.gradle.proto

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.protobuf") version "0.9.4"
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "lorry.folder.items.memogamma"
    compileSdk = 35

    defaultConfig {
        applicationId = "lorry.folder.items.memogamma"
        minSdk = 34
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources.excludes += "META-INF/LICENSE.md"
        resources.excludes += "META-INF/LICENSE.txt"
        resources.excludes += "META-INF/DEPENDENCIES"
        resources.excludes += "META-INF/NOTICE.md"
        resources.excludes += "META-INF/NOTICE.txt"
        resources.excludes += "META-INF/LICENSE-notice.md"
        resources.excludes += "mockito-extensions/org.mockito.plugins.MockMaker"
        resources.excludes += "mozilla/public-suffix-list.txt"
        resources.excludes += "META-INF/io.netty.versions.properties"
        resources.excludes += "META-INF/INDEX.LIST"
    }

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://repository.apache.org/content/repositories/releases/") }
        maven { url = uri("https://maven.google.com/") }
        maven { url = uri("https://jitpack.io") }
        flatDir {
            dirs("libs")
        }
    }

    sourceSets["main"].proto{
        srcDir("src/main/proto")
    }

    sourceSets {
        named("main") {
            java.srcDir("build/generated/source/proto/debug/java")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.14.0"
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

ksp {
    arg("ksp.generated", "$buildDir/generated/source/proto/main/java")
}

afterEvaluate {
    tasks.named("kspDebugKotlin") {
        dependsOn("generateDebugProto")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    ksp(libs.hilt.compiler)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation(libs.hilt.android)
    kspAndroidTest(libs.hilt.android.compiler)
    kspAndroidTest("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)
    implementation("com.google.dagger:hilt-android:2.51.1")
    debugImplementation("androidx.test:core:1.5.0")

    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation(libs.buttontogglegroup)
    implementation("com.github.only52607:compose-floating-window:1.0")
//    implementation(libs.androidx.datastore.preferences)
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    implementation("androidx.datastore:datastore:1.0.0")
    implementation("androidx.datastore:datastore-core:1.0.0")
    implementation("com.google.protobuf:protobuf-javalite:3.14.0") // ou plus récent
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}