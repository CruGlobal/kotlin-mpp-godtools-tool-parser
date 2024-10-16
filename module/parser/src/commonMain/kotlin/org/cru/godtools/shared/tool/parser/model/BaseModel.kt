package org.cru.godtools.shared.tool.parser.model

abstract class BaseModel internal constructor(override val parent: Base? = null) : Base {
    override val stylesParent get() = parent as? Styles ?: parent?.stylesParent
    override val manifest get() = checkNotNull(parent?.manifest) { "No manifest found in model ancestors" }
}
