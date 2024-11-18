plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.tangkelek"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tangkelek"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    viewBinding {
        enable = true
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("com.chauthai.swipereveallayout:swipe-reveal-layout:1.4.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.amitshekhar.android:android-networking:1.0.2")
    implementation ("com.github.d-max:spots-dialog:1.1@aar")
    implementation ("com.eightbitlab:blurview:1.6.6")
    implementation ("com.saadahmedev.popup-dialog:popup-dialog:1.0.5")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("commons-io:commons-io:2.11.0")
}