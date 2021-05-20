package org.cru.godtools.tool.xml

import org.cru.godtools.tool.xml.XmlPullParser.Companion.END_DOCUMENT
import org.cru.godtools.tool.xml.XmlPullParser.Companion.END_TAG
import org.cru.godtools.tool.xml.XmlPullParser.Companion.START_TAG

abstract class SaxXmlPullParser : XmlPullParser {
    private var currentEvent = ParserEvent(XmlPullParser.START_DOCUMENT)
    private val events = mutableListOf<ParserEvent>()

    override fun require(type: Int, namespace: String?, name: String?) = with(currentEvent) {
        if (this.type != type) throw Exception("expected $type")
        if (namespace != null && this.namespace != namespace) throw Exception("expected $namespace")
        if (name != null && this.name != name) throw Exception("expected $name")
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

    private class ParserEvent(
        val type: Int,
        val namespace: String? = null,
        val name: String? = null,
        val content: String? = null
    )

    protected fun enqueueStartTag(namespaceUri: String?, name: String?) {
        events += ParserEvent(START_TAG, namespaceUri, name)
    }

    protected fun enqueueEndTag(namespaceUri: String?, name: String?) {
        events += ParserEvent(END_TAG, namespaceUri, name)
    }

    protected fun enqueueEndDocument() {
        events += ParserEvent(END_DOCUMENT)
    }
}
