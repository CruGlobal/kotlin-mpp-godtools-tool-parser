package org.cru.godtools.shared.tool.parser.xml

import platform.Foundation.NSXMLParser
import platform.Foundation.NSXMLParserDelegateProtocol
import platform.darwin.NSObject

class IosXmlPullParser(parser: NSXMLParser) : SaxXmlPullParser() {
    private val internalParser = InternalXMLParser(parser)

    @Suppress("CONFLICTING_OVERLOADS")
    private inner class InternalXMLParser(parser: NSXMLParser) : NSObject(), NSXMLParserDelegateProtocol {
        // region Active Namespaces
        private val namespaces = mutableMapOf<String, MutableList<String>>()
        override fun parser(parser: NSXMLParser, didStartMappingPrefix: String, toURI: String) {
            namespaces.getOrPut(didStartMappingPrefix) { mutableListOf() }.add(toURI)
        }

        override fun parser(parser: NSXMLParser, didEndMappingPrefix: String) {
            namespaces[didEndMappingPrefix]!!.removeLast()
        }
        // endregion Active Namespaces

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
            map { it.key?.toString().orEmpty().attrToQName() to it.value?.toString().orEmpty() }.toMap()

        private fun String.attrToQName(): QName = when {
            !contains(':') -> QName(local = this)
            else -> {
                val (ns, local) = split(':', limit = 2)
                QName(namespaces[ns]!!.last(), local)
            }
        }

        init {
            parser.delegate = this
            parser.shouldProcessNamespaces = true
            parser.shouldReportNamespacePrefixes = true
            parser.parse()
        }
    }
}
