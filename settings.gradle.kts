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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WuMingTao"
include(":app")
include(":experiment02")
include(":experiment03")
include(":mycalculator")
include(":listviewdemo")
include(":recycleviewdemo")
include(":my_menu")
include(":broadcastreceiver")
include(":filetest")
include(":contentproviderone")
include(":notificationtest")
include(":handlertest")
include(":webviewweather")
include(":sensordemo")
include(":mpandroidcharttest")
include(":sharedpreferencestest")
include(":databaseroomtest")
include(":contentprovidertwo")
include(":cameraalbumtest")
include(":playmusictest")
include(":playvideotest")
include(":databasetest")
