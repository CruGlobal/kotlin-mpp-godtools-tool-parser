package org.cru.godtools.tool.xml

import org.cru.godtools.tool.internal.node.sax.QualifiedTag
import org.cru.godtools.tool.internal.node.sax.SAXOptions
import org.cru.godtools.tool.internal.node.sax.SAXParser

private val SAX_OPTIONS = object : SAXOptions {
    override val xmlns = true
}

class JsXmlPullParser(xml: String) : SaxXmlPullParser() {
    private val internalParser = InternalSAXParser().apply {
        write(xml)
        close()
    }

    private inner class InternalSAXParser : SAXParser(true, SAX_OPTIONS) {
        override fun onopentag(tag: QualifiedTag) {
            enqueueStartTag(tag.uri, tag.name)
        }
    }
}
