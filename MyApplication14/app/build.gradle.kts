import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34
    packaging {
        resources{
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/LICENSE"
        }
    }


    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(files("libs\\commons-io-2.2.jar"))
    implementation(files("libs\\commons-lang3-3.4.jar"))
    implementation(files("libs\\commons-net-3.1.jar"))
    implementation(files("libs\\commons-validator-1.4.0.jar"))
    implementation(files("libs\\core-1.53.0.0.jar"))
    implementation(files("libs\\httpcore-4.3.1.jar"))
    implementation(files("libs\\httpmime-4.3.2.jar"))
    implementation(files("libs\\jackson-annotations-2.2.3.jar"))
    implementation(files("libs\\jackson-core-2.2.3.jar"))
    implementation(files("libs\\jackson-databind-2.2.3.jar"))
    implementation(files("libs\\opencsv-2.2.jar"))
    implementation(files("libs\\pkix-1.53.0.0.jar"))
    implementation(files("libs\\prov-1.53.0.0.jar"))
    implementation(files("libs\\snmp6_1z.jar"))
    implementation(files("libs\\ZSDK_ANDROID_API.jar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}