package org.cru.godtools.tool.xml

import org.cru.godtools.tool.xml.XmlPullParser.Companion.END_DOCUMENT
import org.cru.godtools.tool.xml.XmlPullParser.Companion.END_TAG
import org.cru.godtools.tool.xml.XmlPullParser.Companion.START_TAG

abstract class SaxXmlPullParser : XmlPullParser {
    private var currentEvent = ParserEvent(XmlPullParser.START_DOCUMENT)
    private val events = mutableListOf<ParserEvent>()

    override fun require(type: Int, namespace: String?, name: String?) = with(currentEvent) {
        if (this.type != type) throw Exception("expected $type")
        if (namespace != null && namespace != this.qname?.uri) throw Exception("expected $namespace")
        if (name != null && this.qname?.local != name) throw Exception("expected $name")
    }

    override fun next() = events.removeFirst().also { currentEvent = it }.type

    override fun nextTag(): Int {
        if (next() == XmlPullParser.TEXT && currentEvent.content.isNullOrBlank()) next()
        val event = currentEvent
        if (event.type != START_TAG && event.type != END_TAG) {
            throw Exception("Expected start or end tag. Found: " + event.type)
        }
        return event.type
    }

    override fun getAttributeValue(namespace: String?, name: String): String? {
        val event = currentEvent.takeIf { it.type == START_TAG } ?: throw IndexOutOfBoundsException()
        return event.attrs?.get(QName(namespace, name))
    }

    protected data class QName(val uri: String? = null, val local: String)
    private class ParserEvent(
        val type: Int,
        val qname: QName? = null,
        val content: String? = null,
        val attrs: Map<QName, String>? = null
    )

    protected fun enqueueStartTag(qname: QName, attrs: Map<QName, String>? = null) {
        events += ParserEvent(START_TAG, qname, attrs = attrs)
    }

    protected fun enqueueEndTag(qname: QName) {
        events += ParserEvent(END_TAG, qname)
    }

    protected fun enqueueEndDocument() {
        events += ParserEvent(END_DOCUMENT)
    }
}
