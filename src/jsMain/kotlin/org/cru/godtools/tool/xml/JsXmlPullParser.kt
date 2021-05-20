package org.cru.godtools.tool.xml

import org.cru.godtools.tool.internal.node.sax.Attributes
import org.cru.godtools.tool.internal.node.sax.QualifiedAttribute
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
        override fun onopentag(tag: QualifiedTag) =
            enqueueStartTag(QName(tag.uri, tag.local), attrs = tag.attributes.convert())

        override fun onclosetag(tagName: String) = enqueueEndTag(QName(local = tagName))
    }

    private fun Attributes.convert(): Map<QName, String> =
        (js("Object.values") as (dynamic) -> Array<QualifiedAttribute>)
            .invoke(asDynamic())
            .associateBy({ QName(it.uri?.takeIf { it.isNotBlank() }, it.local) }, { it.value })
}
