package org.cru.godtools.tool.model

import org.cru.godtools.tool.xml.XmlPullParser

abstract class Content : BaseModel {
    internal constructor(parent: Base, parser: XmlPullParser) : super(parent)
}
