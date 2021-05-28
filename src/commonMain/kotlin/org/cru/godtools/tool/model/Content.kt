package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.xml.XmlPullParser

abstract class Content : BaseModel {
    internal constructor(parent: Base, parser: XmlPullParser) : super(parent)

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(parent: Base) : super(parent)
}
