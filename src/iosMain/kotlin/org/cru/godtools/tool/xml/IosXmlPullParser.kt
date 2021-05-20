package org.cru.godtools.tool.xml

import org.cru.godtools.tool.xml.XmlPullParser.Companion.END_DOCUMENT
import org.cru.godtools.tool.xml.XmlPullParser.Companion.END_TAG
import org.cru.godtools.tool.xml.XmlPullParser.Companion.START_DOCUMENT
import org.cru.godtools.tool.xml.XmlPullParser.Companion.START_TAG
import org.cru.godtools.tool.xml.XmlPullParser.Companion.TEXT
import platform.Foundation.NSXMLParser
import platform.Foundation.NSXMLParserDelegateProtocol
import platform.darwin.NSObject

class IosXmlPullParser(parser: NSXMLParser) : XmlPullParser {
    private val internalParser = InternalXMLParser(parser)

    private val currentEvent get() = internalParser.currentEvent

    override fun require(type: Int, namespace: String?, name: String?) = with(currentEvent) {
        if (this.type != type) throw Exception("expected $type")
        if (namespace != null && this.namespace != namespace) throw Exception("expected $namespace")
        if (name != null && this.name != name) throw Exception("expected $name")
    }

    override fun next() = internalParser.nextEvent().type

    override fun nextTag(): Int {
        if (next() == TEXT && currentEvent.content.isNullOrBlank()) next()
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

    @Suppress("CONFLICTING_OVERLOADS")
    private class InternalXMLParser(parser: NSXMLParser) : NSObject(), NSXMLParserDelegateProtocol {
        private val events = mutableListOf<ParserEvent>()
        var currentEvent = ParserEvent(START_DOCUMENT)

        fun nextEvent() = events.removeFirst().also { currentEvent = it }

        override fun parser(
            parser: NSXMLParser,
            didStartElement: String,
            namespaceURI: String?,
            qualifiedName: String?,
            attributes: Map<Any?, *>
        ) {
            events += ParserEvent(START_TAG, namespaceURI, didStartElement)
        }

        override fun parser(parser: NSXMLParser, didEndElement: String, namespaceURI: String?, qualifiedName: String?) {
            events += ParserEvent(END_TAG, namespaceURI, didEndElement)
        }

        override fun parserDidEndDocument(parser: NSXMLParser) {
            events += ParserEvent(END_DOCUMENT)
        }

        init {
            parser.delegate = this
            parser.shouldProcessNamespaces = true
            parser.parse()
        }
    }
}
