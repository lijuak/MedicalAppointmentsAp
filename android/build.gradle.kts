// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Debes definir estas versiones en el archivo libs.versions.toml
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // El plugin de Google Services se declara de forma diferente con los catálogos de versiones
    // Por ahora lo dejamos así, pero lo ideal es moverlo al catálogo.
    id("com.google.gms.google-services") version "4.4.2" apply false
}
    