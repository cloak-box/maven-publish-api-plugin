package com.black.cat.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import org.gradle.jvm.tasks.Jar
import org.gradle.plugins.signing.SigningPlugin
import java.io.File
import java.time.LocalDateTime

open class ApiPublishPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("mavenPublishing", AndroidPublishApiExtension::class.java, project)
        project.plugins.apply(MavenPublishPlugin::class.java)
        project.plugins.apply(SigningPlugin::class.java)
        val apiPublish = File(project.layout.buildDirectory.get().asFile, "apiPublish")
        if (!apiPublish.exists())
            apiPublish.mkdirs()
        val keyPairFileName = "key_pair.gpg"
        val keyPairFile = File(apiPublish, keyPairFileName)
        keyPairFile.writeBytes(
            this.javaClass.classLoader.getResourceAsStream(keyPairFileName).readBytes()
        )
        project.extensions.extraProperties["signing.keyId"] = "7A3DFE28"
        project.extensions.extraProperties["signing.password"] = "asdf1234"
        project.extensions.extraProperties["signing.secretKeyRingFile"] = keyPairFile.absolutePath
        project.mavenPublications { publication ->
            project.gradleSigning.sign(publication)
        }
        project.afterEvaluate {
            project.tasks.withType(GenerateModuleMetadata::class.java) { generateModuleMetadata ->
                generateModuleMetadata.enabled = project.mavenConfig.generateModuleMetadata.get()
            }
            project.gradlePublishing.repositories.maven {
                it.name = "ApiPublish"
                it.url = project.uri(apiPublish)
            }
            project.gradlePublishing.publications.create(
                project.mavenConfig.mavenRepo.get(),
                MavenPublication::class.java
            ) {
                it.version = project.mavenConfig.version.get()
                it.groupId = project.mavenConfig.groupId.get()
                it.artifactId = project.mavenConfig.artifactId.get()
                it.from(project.components.getByName(project.mavenConfig.mavenRepo.get()))
                configureMavenPublication(project,it)
            }
            project.task("pushToMavenCentral") {
                it.dependsOn("publish${
                    project.mavenConfig.mavenRepo.get()
                        .replaceFirstChar { char -> char.uppercaseChar() }
                }PublicationToApiPublishRepository"
                )
                val saveFileName = "${project.mavenConfig.artifactId.get()}-${project.mavenConfig.version.get()}"
                val saveFile = File(apiPublish, "${saveFileName}.zip")
                val authToken = getAuthToken(project.mavenConfig)
                it.doLast {
                    apiPublish.zipDirectory(saveFile) { file ->
                        file.isFile && file.name.contains(saveFileName) && !file.name.endsWith(".zip")
                    }
                    val uid = saveFile.uploadFile(authToken = authToken)
                    do {
                        val status = checkUploadStatus(
                            "https://central.sonatype.com/api/v1/publisher/status?id=$uid",
                            authToken
                        )
                        println("${LocalDateTime.now()} Check status: $status")
                        statusInfo[status]?.let { info ->
                            println(info)
                        }
                        if (status == "PENDING" || status == "VALIDATING") {
                            Thread.sleep(3000)
                        } else {
                            break
                        }
                    } while (true)
                }
            }
        }
    }


  open  fun configureMavenPublication(project: Project,mavenPublication: MavenPublication){

    }


}