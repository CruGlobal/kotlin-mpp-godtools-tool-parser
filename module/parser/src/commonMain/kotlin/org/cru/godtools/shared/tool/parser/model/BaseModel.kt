package org.cru.godtools.shared.tool.parser.model

abstract class BaseModel internal constructor(override val parent: Base? = null) : Base {
    override val manifest get() = checkNotNull(parent?.manifest) { "No manifest found in model ancestors" }
}
