package org.cru.godtools.tool.model

import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

class Flow : Content {
    internal companion object {
        internal const val XML_FLOW = "flow"
        private const val XML_ITEM = "item"
    }

    val items: List<Item>

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_FLOW)

        items = mutableListOf()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_CONTENT -> when (parser.name) {
                    XML_ITEM -> items += Item(this, parser)
                    else -> {
                        val item = Item(this) { parser.parseContentElement(it)?.takeUnless { it.isIgnored } }
                        if (item.content.isNotEmpty()) items += item
                    }
                }
            }
        }
    }

    class Item : BaseModel, Parent {
        val flow: Flow

        override val content: List<Content>

        internal constructor(flow: Flow, parser: XmlPullParser) : super(flow) {
            parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_ITEM)
            this.flow = flow
            content = parseContent(parser)
        }

        internal constructor(flow: Flow, content: (Item) -> Content?) : super(flow) {
            this.flow = flow
            this.content = listOfNotNull(content(this))
        }
    }
}
