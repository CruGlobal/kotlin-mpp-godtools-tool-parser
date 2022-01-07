package org.cru.godtools.tool.model

import org.cru.godtools.tool.FEATURE_FLOW
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.Dimension.Companion.toDimensionOrNull
import org.cru.godtools.tool.model.Flow.Companion.DEFAULT_ITEM_WIDTH
import org.cru.godtools.tool.model.Flow.Companion.DEFAULT_ROW_GRAVITY
import org.cru.godtools.tool.model.Gravity.Companion.toGravityOrNull
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_ROW_GRAVITY = "row-gravity"
private const val XML_ITEM = "item"

class Flow : Content {
    internal companion object {
        internal const val XML_FLOW = "flow"
        private const val XML_ITEM_WIDTH = "item-width"

        internal val DEFAULT_ITEM_WIDTH = Dimension.Percent(1f)
        internal val DEFAULT_ROW_GRAVITY = Gravity.Horizontal.START
    }

    @VisibleForTesting
    internal val itemWidth: Dimension

    internal val rowGravity: Gravity.Horizontal

    val items: List<Item>

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_FLOW)

        itemWidth = parser.getAttributeValue(XML_ITEM_WIDTH).toDimensionOrNull() ?: DEFAULT_ITEM_WIDTH
        rowGravity = parser.getAttributeValue(XML_ROW_GRAVITY)?.toGravityOrNull()?.horizontal ?: DEFAULT_ROW_GRAVITY

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
        companion object {
            private const val XML_WIDTH = "width"
        }

        val flow: Flow

        private val _width: Dimension?
        internal val width get() = _width ?: flow.itemWidth

        override val content: List<Content>

        internal constructor(flow: Flow, parser: XmlPullParser) : super(flow) {
            parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_ITEM)
            this.flow = flow

            _width = parser.getAttributeValue(XML_WIDTH).toDimensionOrNull()

            content = parseContent(parser)
        }

        internal constructor(flow: Flow, content: (Item) -> Content?) : super(flow) {
            this.flow = flow
            _width = null
            this.content = listOfNotNull(content(this))
        }
    }
}

val Flow?.rowGravity get() = this?.rowGravity ?: DEFAULT_ROW_GRAVITY
val Flow.Item?.width get() = this?.width ?: DEFAULT_ITEM_WIDTH
