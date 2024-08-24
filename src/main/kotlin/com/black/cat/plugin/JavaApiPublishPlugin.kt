package com.black.cat.plugin

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar

open class JavaApiPublishPlugin : ApiPublishPlugin() {
    override fun configureMavenPublication(project: Project, mavenPublication: MavenPublication) {
        withJavaSourcesJar(project.mavenConfig.poublicSourcesJar.get(), project,mavenPublication)
        withJavadocJar(project.mavenConfig.publishJavadocJar.get(), project,mavenPublication)
    }


    private fun withJavaSourcesJar(enabled: Boolean, project: Project, mavenPublication: MavenPublication) {
        if (enabled) {
            project.extensions.getByType(JavaPluginExtension::class.java).withSourcesJar()
        } else {
            val task = project.tasks.register("emptySourcesJar", Jar::class.java) {
                it.archiveClassifier.set("sources")
            }
            mavenPublication.artifact(task)
        }
    }

    private fun withJavadocJar(enabled: Boolean, project: Project, mavenPublication: MavenPublication) {
        if (enabled) {
            project.extensions.getByType(JavaPluginExtension::class.java).withJavadocJar()
        } else {
            val task = project.tasks.register("emptyJavadocJar", Jar::class.java) {
                it.archiveClassifier.set("javadoc")
            }
            mavenPublication.artifact(task)
        }
    }
}