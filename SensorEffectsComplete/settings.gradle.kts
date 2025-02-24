pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")  // Ensure JitPack is included for external dependencies
    }
    versionCatalogs {
        create("libs") {
            // Only use a single 'from' call with the correct path to your TOML file
            //from(files("gradle/libs.versions.toml"))
        }
    }
}


rootProject.name = "SensorEffectsComplete"
include(":app")
 