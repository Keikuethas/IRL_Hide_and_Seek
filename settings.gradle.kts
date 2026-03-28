import org.gradle.kotlin.dsl.maven

pluginManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.google.com/")
        }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "IRL Hide and Seek"
include(":app")
 