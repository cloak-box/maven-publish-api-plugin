package com.black.cat.plugin

import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import java.io.File
import java.time.LocalDateTime

open class AndroidApiPublishPlugin : ApiPublishPlugin() {
    override fun apply(project: Project) {
        super.apply(project)
        project.plugins.withId("com.android.library") {
            project.androidComponents.finalizeDsl {
                project.libraryExtension.publishing {
                    singleVariant(project.mavenConfig.mavenRepo.get()) {
                        if (project.mavenConfig.poublicSourcesJar.get()) {
                            withSourcesJar()
                        }
                        if (project.mavenConfig.publishJavadocJar.get()) {
                            withSourcesJar()
                        }
                    }
                }
            }
        }

    }
}