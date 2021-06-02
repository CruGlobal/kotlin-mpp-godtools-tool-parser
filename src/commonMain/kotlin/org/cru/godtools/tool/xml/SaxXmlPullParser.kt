package org.cru.godtools.tool.xml

import org.cru.godtools.tool.xml.XmlPullParser.Companion.END_DOCUMENT
import org.cru.godtools.tool.xml.XmlPullParser.Companion.END_TAG
import org.cru.godtools.tool.xml.XmlPullParser.Companion.START_TAG
import org.cru.godtools.tool.xml.XmlPullParser.Companion.TEXT

abstract class SaxXmlPullParser : XmlPullParser {
    private var currentEvent = ParserEvent(XmlPullParser.START_DOCUMENT)
    private val events = mutableListOf<ParserEvent>()

    override val eventType get() = currentEvent.type
    override val namespace get() = currentEvent.qname?.uri
    override val name get() = currentEvent.qname?.local

    override fun require(type: Int, namespace: String?, name: String?) = with(currentEvent) {
        if (this.type != type) throw Exception("expected $type")
        if (namespace != null && namespace != this.qname?.uri) throw Exception("expected $namespace")
        if (name != null && this.qname?.local != name) throw Exception("expected $name")
    }

    override fun next() = events.removeFirst().also { currentEvent = it }.type

    override fun nextTag(): Int {
        if (next() == TEXT && currentEvent.content.isNullOrBlank()) next()
        val event = currentEvent
        if (event.type != START_TAG && event.type != END_TAG) {
            throw XmlPullParserException("Expected start or end tag. Found: ${event.type}")
        }
        return event.type
    }

    override fun nextText(): String {
        require(START_TAG)
        return when (next()) {
            TEXT -> currentEvent.content.orEmpty()
                .also { check(next() == END_TAG) }
            END_TAG -> ""
            else -> throw XmlPullParserException("parser must be on START_TAG or TEXT to read text")
        }
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
        enqueueEvent(ParserEvent(START_TAG, qname, attrs = attrs))
    }

    private val accumulatedText = StringBuilder()
    protected fun enqueueText(text: String) {
        accumulatedText.append(text)
    }

    protected fun enqueueEndTag(qname: QName) {
        enqueueEvent(ParserEvent(END_TAG, qname))
    }

    protected fun enqueueEndDocument() {
        enqueueEvent(ParserEvent(END_DOCUMENT))
    }

    private fun enqueueEvent(event: ParserEvent) {
        if (accumulatedText.isNotEmpty()) {
            events += ParserEvent(TEXT, content = accumulatedText.toString())
            accumulatedText.clear()
        }
        events += event
    }
}
