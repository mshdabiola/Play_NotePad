@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("mshdabiola.android.library")
    id("mshdabiola.android.library.compose")
    //alias(libs.plugins.google.services)
}

android {
    namespace = "com.mshdabiola.designsystem"

}


dependencies {

    //implementation (project( ":model"))

    testImplementation(libs.junit4)

}