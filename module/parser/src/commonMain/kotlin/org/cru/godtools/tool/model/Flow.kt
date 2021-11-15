package org.cru.godtools.tool.model

import org.cru.godtools.tool.FEATURE_FLOW
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.Dimension.Companion.toDimensionOrNull
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_COLUMNS = "columns"
private const val XML_ITEM = "item"
private const val XML_ITEM_WIDTH = "width"

class Flow : Content {
    internal companion object {
        internal const val XML_FLOW = "flow"

        @VisibleForTesting
        internal const val DEFAULT_COLUMNS = 1
    }

    val columns: Int
    private val itemWidth get() = Dimension.Percent(1f / columns)

    val items: List<Item>

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_FLOW)

        columns = parser.getAttributeValue(XML_COLUMNS)?.toIntOrNull()?.takeUnless { it < 1 } ?: DEFAULT_COLUMNS

        items = mutableListOf()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_CONTENT -> when (parser.name) {
                    XML_ITEM -> items += Item(this, parser)
                }
            }

            // we haven't handled this tag yet, if it's a content element wrap it in an Item.
            if (parser.eventType == XmlPullParser.START_TAG) {
                val item = Item(this) { parser.parseContentElement(it)?.takeUnless { it.isIgnored } }
                if (item.content.isNotEmpty()) items += item
            }
        }
    }

    override val isIgnored get() = FEATURE_FLOW !in ParserConfig.supportedFeatures || super.isIgnored

    class Item : BaseModel, Parent {
        val flow: Flow

        private val _width: Dimension?
        val width get() = _width ?: flow.itemWidth

        override val content: List<Content>

        internal constructor(flow: Flow, parser: XmlPullParser) : super(flow) {
            parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_ITEM)
            this.flow = flow

            _width = parser.getAttributeValue(XML_ITEM_WIDTH).toDimensionOrNull()

            content = parseContent(parser)
        }

        internal constructor(flow: Flow, content: (Item) -> Content?) : super(flow) {
            this.flow = flow
            _width = null
            this.content = listOfNotNull(content(this))
        }
    }
}
