# maven-publish-api-plugin
Gradle plugin that creates a publish task to automatically upload all of your Java, Kotlin or Android libraries to any Maven instance. 

# How to use 

### step 1
```
buildscript {
    dependencies {
    classpath("io.github.cloak-box.plugin:maven-api-plugin:1.0.0.2")
    }
}
```
### step 2
```
plugins {
    //for publish android project
    id("com.black.cat.plugin.AndroidApiPublishPlugin")
    //for publish java project
    id("com.black.cat.plugin.JavaApiPublishPlugin")
}
```
### step 3
# for gradle.kts
```
mavenPublishing {
  mavenConfig {
    groupId = "groupId"
    artifactId = "artifactId"
    version = "version
    publishJavadocJar = false
    poublicSourcesJar = false
    mavenRepo = "sdk"
    mavenCentralUsername = "mavenCentralUsername"
    mavenCentralPassword = "mavenCentralPassword"
    pom {
      name.set("cloak box")
      description.set("A description of what my library does.")
      inceptionYear.set("2020")
      url.set("https://github.com/cloak-box/Vbox")
      licenses {
        license {
          name.set("GNU GENERAL PUBLIC LICENSE , Version 3, 29 June 2007")
          url.set("https://www.gnu.org/licenses/gpl-3.0.en.html#license-text")
          distribution.set("https://www.gnu.org/licenses/gpl-3.0.en.html#license-text")
        }
      }
      developers {
        developer {
          id.set("cloak box")
          name.set("cloak box")
          url.set("https://github.com/cloak-box")
        }
      }
      scm {
        url.set("https://github.com/cloak-box/Vbox")
        connection.set("scm:git:git://github.com/cloak-box/Vbox.git")
        developerConnection.set("scm:git:ssh://git@github.com/cloak-box/Vbox.git")
      }
    }
  }
}
```
# for gradle 
```
mavenPublishing {
    mavenConfig { config->
        config.getGroupId().set("group")
        config.getArtifactId().set("artifactId")
        config.getVersion().set("version")
        config.getPublishJavadocJar().set(false)
        config.getPoublicSourcesJar().set(false)
        config.getMavenRepo().set("release")
        config.getMavenCentralUsername().set("mavenCentralUsername")
        config.getMavenCentralPassword().set("mavenCentralPassword")
        pom {pomConfig->
            pomConfig.getName().set("cloak box")
            pomConfig.getDescription().set("A description of what my library does.")
            pomConfig.getInceptionYear().set("2020")
            pomConfig.getUrl().set("https://github.com/cloak-box/Vbox")
            pomConfig.licenses {
                license {
                    name.set("GNU GENERAL PUBLIC LICENSE , Version 3, 29 June 2007")
                    url.set("https://www.gnu.org/licenses/gpl-3.0.en.html#license-text")
                    distribution.set("https://www.gnu.org/licenses/gpl-3.0.en.html#license-text")
                }
            }
            pomConfig.developers {
                developer {
                    id.set("cloak box")
                    name.set("cloak box")
                    url.set("https://github.com/cloak-box")
                }
            }
            pomConfig.scm {
                url.set("https://github.com/cloak-box/Vbox")
                connection.set("scm:git:git://github.com/cloak-box/Vbox.git")
                developerConnection.set("scm:git:ssh://git@github.com/cloak-box/Vbox.git")
            }
        }
    }
}
```
# step 4
./gradlew pushToMavenCentral

# License
[GPL3](LICENSE) 
