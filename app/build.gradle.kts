import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// Stable signing: local properties or environment variables in the protected CI job.
// IMPORTANT: every release MUST be signed with the same key, otherwise the in-app updater's
// install will fail with INSTALL_FAILED_UPDATE_INCOMPATIBLE.
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) keystorePropsFile.inputStream().use { load(it) }
}
val signingEnv = mapOf(
    "storeFile" to "DRIVEHOME_KEYSTORE_FILE",
    "storePassword" to "KEYSTORE_PASSWORD",
    "keyAlias" to "KEY_ALIAS",
    "keyPassword" to "KEY_PASSWORD"
)
val stableSigningRequested = System.getenv("DRIVEHOME_SIGNING_REQUIRED") == "true"
if (stableSigningRequested) {
    signingEnv.forEach { (property, variable) ->
        val value = System.getenv(variable)
        require(!value.isNullOrBlank()) { "Missing required signing variable: $variable" }
        keystoreProps.setProperty(property, value)
    }
}
if (keystoreProps.isNotEmpty()) {
    signingEnv.keys.forEach { property ->
        require(!keystoreProps.getProperty(property).isNullOrBlank()) {
            "Missing signing property: $property"
        }
    }
    require(project.file(keystoreProps.getProperty("storeFile")).isFile) {
        "Signing keystore file does not exist"
    }
}

android {
    namespace = "com.minimal.carlauncher"
    compileSdk = 35

    defaultConfig {
        applicationId = "es.gitcalde.drivehome"
        minSdk = 29
        // NOTE: deliberately NOT targetSdk 36. Android 16 ignores android:screenOrientation for
        // apps targeting 36 on displays with smallestWidth >= 600dp - which is exactly a head unit.
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        // DriveHome release repository.
        buildConfigField("String", "GITHUB_OWNER", "\"gitcalde\"")
        buildConfigField("String", "GITHUB_REPO", "\"drivehome\"")

    }

    signingConfigs {
        if (keystoreProps.isNotEmpty()) {
            create("release") {
                storeFile = file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (keystoreProps.isNotEmpty()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        debug {
            // Protected personal builds use the same certificate as releases.
            // Ordinary PR builds retain Android's temporary debug signing.
            if (keystoreProps.isNotEmpty()) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.osmdroid)

    testImplementation(libs.junit)
    testImplementation(libs.json)
}
