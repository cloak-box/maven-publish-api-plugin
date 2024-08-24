package com.black.cat.plugin

import org.gradle.api.Action
import org.gradle.api.Project

abstract class AndroidPublishApiExtension(private val project: Project) {
    internal val mavenConfig = project.objects.property(MavenConfig::class.java)
    fun mavenConfig(action: Action<MavenConfig>) {
        mavenConfig.set(MavenConfig(project))
        mavenConfig.finalizeValueOnRead()
        action.execute(mavenConfig.get())
        mavenConfig.get().finalizeValueOnRead()
    }
}