package com.black.cat.plugin

import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension


internal inline val Project.androidPublishApiExtension: AndroidPublishApiExtension
    get() = extensions.getByType(AndroidPublishApiExtension::class.java)

internal inline val Project.gradleSigning: SigningExtension
    get() = extensions.getByType(SigningExtension::class.java)

internal inline val Project.gradlePublishing: PublishingExtension
    get() = extensions.getByType(PublishingExtension::class.java)

internal inline val Project.libraryExtension: LibraryExtension
    get() = extensions.getByType(LibraryExtension::class.java)

internal inline val Project.mavenConfig: MavenConfig
    get() = extensions.getByType(AndroidPublishApiExtension::class.java).mavenConfig.get()

internal fun Project.mavenPublications(action: Action<MavenPublication>) {
    gradlePublishing.publications.withType(MavenPublication::class.java).configureEach(action)
}

internal inline val Project.androidComponents: AndroidComponentsExtension<*, *, *>
    get() = extensions.getByType(AndroidComponentsExtension::class.java)