package org.cru.godtools.tool.model

import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.skipTag

class Text : BaseModel {
    companion object {
        internal const val XML_TEXT = "text"
    }

    val text: String?

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_TEXT)
        text = parser.nextText()
    }
}

internal fun XmlPullParser.parseTextChild(
    parent: Base,
    parentNamespace: String?,
    parentName: String,
    block: () -> Unit = { }
): Text? {
    require(XmlPullParser.START_TAG, parentNamespace, parentName)

    // process any child elements
    var text: Text? = null
    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) continue

        // execute any custom parsing logic from the call-site
        // if the block consumes the tag, the parser will be on an END_TAG after returning
        block()
        if (eventType == XmlPullParser.END_TAG) continue

        // parse text node
        when {
            namespace == XMLNS_CONTENT && name == Text.XML_TEXT -> text = Text(parent, this)
            else -> skipTag()
        }
    }
    return text
}
