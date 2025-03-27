@file:JvmMultifileClass
@file:JvmName("FlowKt")

package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.VisibleForTesting
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.native.HiddenFromObjC
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_FLOW
import org.cru.godtools.shared.tool.parser.expressions.Expression
import org.cru.godtools.shared.tool.parser.model.Dimension.Companion.toDimensionOrNull
import org.cru.godtools.shared.tool.parser.model.Flow.Companion.DEFAULT_ITEM_WIDTH
import org.cru.godtools.shared.tool.parser.model.Flow.Companion.DEFAULT_ROW_GRAVITY
import org.cru.godtools.shared.tool.parser.model.Gravity.Companion.toGravityOrNull
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.getDeviceAttributeValue
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_ROW_GRAVITY = "row-gravity"
private const val XML_ITEM = "item"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class Flow : Content {
    internal companion object {
        internal const val XML_FLOW = "flow"
        private const val XML_ITEM_WIDTH = "item-width"

        internal val DEFAULT_ITEM_WIDTH = Dimension.Percent(1f)
        internal val DEFAULT_ROW_GRAVITY = Gravity.Horizontal.START
    }

    @VisibleForTesting
    internal val itemWidth: Dimension

    val rowGravity: Gravity.Horizontal

    @JsExport.Ignore
    @JsName("_items")
    val items: List<Item>

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_FLOW)

        itemWidth =
            parser.getDeviceAttributeValue(manifest.config, XML_ITEM_WIDTH).toDimensionOrNull() ?: DEFAULT_ITEM_WIDTH
        rowGravity = parser.getDeviceAttributeValue(manifest.config, XML_ROW_GRAVITY)?.toGravityOrNull()?.horizontal
            ?: DEFAULT_ROW_GRAVITY

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

    override val isIgnored get() = !manifest.config.supportsFeature(FEATURE_FLOW) || super.isIgnored

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("items")
    val jsItems get() = items.toTypedArray()
    // endregion Kotlin/JS interop

    class Item : BaseModel, Parent, Visibility {
        companion object {
            private const val XML_WIDTH = "width"
        }

        val flow: Flow

        @Suppress("ktlint:standard:property-naming") // https://github.com/pinterest/ktlint/issues/2448
        private val _width: Dimension?
        val width get() = _width ?: flow.itemWidth

        override val invisibleIf: Expression?
        override val goneIf: Expression?

        override val content: List<Content>

        internal constructor(flow: Flow, parser: XmlPullParser) : super(flow) {
            parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_ITEM)
            this.flow = flow

            _width = parser.getDeviceAttributeValue(manifest.config, XML_WIDTH).toDimensionOrNull()

            parser.parseVisibilityAttrs { invisibleIf, goneIf ->
                this.invisibleIf = invisibleIf?.takeIf { it.isValid() }
                this.goneIf = goneIf?.takeIf { it.isValid() }
            }

            content = parseContent(parser)
        }

        internal constructor(flow: Flow, content: (Item) -> Content?) : super(flow) {
            this.flow = flow
            _width = null
            invisibleIf = null
            goneIf = null
            this.content = listOfNotNull(content(this))
        }
    }
}
