package org.cru.godtools.shared.tool.parser.model.tract

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC
import org.cru.godtools.shared.tool.parser.internal.AndroidColorInt
import org.cru.godtools.shared.tool.parser.model.BaseModel
import org.cru.godtools.shared.tool.parser.model.Button
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.Parent
import org.cru.godtools.shared.tool.parser.model.Styles
import org.cru.godtools.shared.tool.parser.model.TRANSPARENT
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.WHITE
import org.cru.godtools.shared.tool.parser.model.XML_DISMISS_LISTENERS
import org.cru.godtools.shared.tool.parser.model.XML_LISTENERS
import org.cru.godtools.shared.tool.parser.model.parseContent
import org.cru.godtools.shared.tool.parser.model.parseTextChild
import org.cru.godtools.shared.tool.parser.model.toEventIds
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser

private const val XML_TITLE = "title"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class Modal : BaseModel, Parent, Styles {
    internal companion object {
        internal const val XML_MODAL = "modal"
    }

    val page: TractPage
    val id get() = "${page.id}-$position"
    private val position get() = page.modals.indexOf(this)

    val title: Text?
    override val content: List<Content>

    @JsName("_listeners")
    val listeners: Set<EventId>
    @JsName("_dismissListeners")
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
                    XML_TITLE -> title = parser.parseTextChild(this@Modal, XMLNS_TRACT, XML_TITLE)
                }
            }
        }
        this.title = title
    }

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("dismissListeners")
    val jsDismissListeners get() = dismissListeners.toTypedArray()

    @HiddenFromObjC
    @JsName("listeners")
    val jsListeners get() = listeners.toTypedArray()
    // endregion Kotlin/JS interop
}
