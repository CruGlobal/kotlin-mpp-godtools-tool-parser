package org.cru.godtools.tool.xml

import platform.Foundation.NSXMLParser
import platform.Foundation.NSXMLParserDelegateProtocol
import platform.darwin.NSObject

class IosXmlPullParser(parser: NSXMLParser) : SaxXmlPullParser() {
    private val internalParser = InternalXMLParser(parser)

    @Suppress("CONFLICTING_OVERLOADS")
    private inner class InternalXMLParser(parser: NSXMLParser) : NSObject(), NSXMLParserDelegateProtocol {
        override fun parser(
            parser: NSXMLParser,
            didStartElement: String,
            namespaceURI: String?,
            qualifiedName: String?,
            attributes: Map<Any?, *>
        ) = enqueueStartTag(QName(namespaceURI, didStartElement), attrs = attributes.convert())

        override fun parser(parser: NSXMLParser, didEndElement: String, namespaceURI: String?, qualifiedName: String?) =
            enqueueEndTag(QName(namespaceURI, didEndElement))

        override fun parser(parser: NSXMLParser, foundCharacters: String) {
            enqueueText(foundCharacters)
        }

        override fun parserDidEndDocument(parser: NSXMLParser) = enqueueEndDocument()

        private fun Map<Any?, *>.convert() =
            map { QName(local = it.key?.toString().orEmpty()) to it.value?.toString().orEmpty() }.toMap()

        init {
            parser.delegate = this
            parser.shouldProcessNamespaces = true
            parser.parse()
        }
    }
}
