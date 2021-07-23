package org.cru.godtools.tool.model

abstract class BaseModel internal constructor(private val parent: Base? = null) : Base {
    override val stylesParent get() = parent as? Styles ?: parent?.stylesParent
    override val manifest get() = checkNotNull(parent?.manifest) { "No manifest found in model ancestors" }
}
