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
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://repository.apache.org/content/repositories/releases/") }
        maven { url = uri("https://maven.google.com/") }
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "MemoGamma"
include(":app")
 