package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.model.Base
import org.cru.godtools.tool.model.BaseModel
import org.cru.godtools.tool.model.Button
import org.cru.godtools.tool.model.Content
import org.cru.godtools.tool.model.EventId
import org.cru.godtools.tool.model.Parent
import org.cru.godtools.tool.model.Styles
import org.cru.godtools.tool.model.TEXT_SIZE_BASE
import org.cru.godtools.tool.model.TEXT_SIZE_MODAL
import org.cru.godtools.tool.model.TEXT_SIZE_MODAL_TITLE
import org.cru.godtools.tool.model.TRANSPARENT
import org.cru.godtools.tool.model.Text
import org.cru.godtools.tool.model.WHITE
import org.cru.godtools.tool.model.XML_DISMISS_LISTENERS
import org.cru.godtools.tool.model.XML_LISTENERS
import org.cru.godtools.tool.model.parseContent
import org.cru.godtools.tool.model.parseTextChild
import org.cru.godtools.tool.model.stylesOverride
import org.cru.godtools.tool.model.textScale
import org.cru.godtools.tool.model.toEventIds
import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_TITLE = "title"

class Modal : BaseModel, Parent, Styles {
    internal companion object {
        internal const val XML_MODAL = "modal"
    }

    val page: TractPage
    val id get() = "${page.id}-$position"
    private val position get() = page.modals.indexOf(this)

    private val titleParent by lazy { stylesOverride(TEXT_SIZE_MODAL_TITLE.toDouble() / TEXT_SIZE_MODAL) }
    val title: Text?
    override val content: List<Content>

    val listeners: Set<EventId>
    val dismissListeners: Set<EventId>

    @get:AndroidColorInt
    override val primaryColor get() = TRANSPARENT
    @get:AndroidColorInt
    override val primaryTextColor get() = WHITE

    @get:AndroidColorInt
    override val buttonColor get() = WHITE
    override val buttonStyle get() = Button.Style.OUTLINED

    override val textAlign get() = Text.Align.CENTER
    @get:AndroidColorInt
    override val textColor get() = WHITE
    override val textScale get() = stylesParent.textScale * TEXT_SIZE_MODAL / TEXT_SIZE_BASE

    internal constructor(parent: TractPage, parser: XmlPullParser) : super(parent) {
        page = parent

        parser.require(XmlPullParser.START_TAG, XMLNS_TRACT, XML_MODAL)

        listeners = parser.getAttributeValue(XML_LISTENERS).toEventIds().toSet()
        dismissListeners = parser.getAttributeValue(XML_DISMISS_LISTENERS).toEventIds().toSet()

        // process any child elements
        var title: Text? = null
        content = parseContent(parser) {
            when (parser.namespace) {
                XMLNS_TRACT -> when (parser.name) {
                    XML_TITLE -> title = parser.parseTextChild(titleParent, XMLNS_TRACT, XML_TITLE)
                }
            }
        }
        this.title = title
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(page: TractPage = TractPage(), title: (Base) -> Text?) : super(page) {
        this.page = page
        this.title = title.invoke(titleParent)
        content = emptyList()
        listeners = emptySet()
        dismissListeners = emptySet()
    }
}
