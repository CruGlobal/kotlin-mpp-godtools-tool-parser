package org.cru.godtools.shared.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty

open class KotlinAntlrExtension internal constructor(objectFactory: ObjectFactory) {
    val packageName: Property<String?> = objectFactory.property<String?>().apply { convention(null) }
}
