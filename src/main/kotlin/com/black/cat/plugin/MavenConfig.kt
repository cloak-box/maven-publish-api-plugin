package com.black.cat.plugin

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPom
import org.gradle.util.GradleVersion

open class MavenConfig(private val project: Project) {

    val artifactId: Property<String> = project.objects.property(String::class.java)
    val groupId: Property<String> = project.objects.property(String::class.java)
    val version: Property<String> = project.objects.property(String::class.java)
    val mavenRepo: Property<String> = project.objects.property(String::class.java)
    val poublicSourcesJar: Property<Boolean> = project.objects.property(Boolean::class.java)
        .convention(false)
    val publishJavadocJar: Property<Boolean> = project.objects.property(Boolean::class.java)
        .convention(false)
    val generateModuleMetadata: Property<Boolean> = project.objects.property(Boolean::class.java)
        .convention(false)
   val mavenCentralUsername: Property<String> =  project.objects.property(String::class.java)
   val mavenCentralPassword: Property<String> =  project.objects.property(String::class.java)

    internal fun finalizeValueOnRead() {
        artifactId.finalizeValueOnRead()
        groupId.finalizeValueOnRead()
        version.finalizeValueOnRead()
        mavenRepo.finalizeValueOnRead()
        poublicSourcesJar.finalizeValueOnRead()
        publishJavadocJar.finalizeValueOnRead()
        generateModuleMetadata.finalizeValueOnRead()
        mavenCentralPassword.finalizeValueOnRead()
        mavenCentralUsername.finalizeValueOnRead()
    }

    fun pom(configure: Action<in MavenPom>) {
        project.mavenPublications { publication ->
            if (GradleVersion.current() >= GradleVersion.version("8.8-rc-1")) {
                publication.pom(configure)
            } else {
                project.afterEvaluate {
                    publication.pom(configure)
                }
            }
        }
    }
}