package org.cru.godtools.tool.model

import org.cru.godtools.expressions.Expression
import org.cru.godtools.expressions.toExpressionOrNull
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.REGEX_SEQUENCE_SEPARATOR
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.model.DeviceType.Companion.toDeviceTypes
import org.cru.godtools.tool.model.tips.InlineTip
import org.cru.godtools.tool.model.tips.Tip
import org.cru.godtools.tool.model.tips.XMLNS_TRAINING
import org.cru.godtools.tool.state.State
import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_REQUIRED_FEATURES = "required-features"
private const val XML_RESTRICT_TO = "restrictTo"
private const val XML_VERSION = "version"
private const val XML_INVISIBLE_IF = "invisible-if"
private const val XML_GONE_IF = "gone-if"

abstract class Content : BaseModel {
    private val version: Int
    private val restrictTo: Set<DeviceType>
    private val requiredFeatures: Set<String>

    private val invisibleIf: Expression?
    private val goneIf: Expression?

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent) {
        version = parser.getAttributeValue(null, XML_VERSION)?.toIntOrNull() ?: SCHEMA_VERSION
        restrictTo = parser.getAttributeValue(XML_RESTRICT_TO)?.toDeviceTypes() ?: DeviceType.ALL
        requiredFeatures = parser.getAttributeValue(XML_REQUIRED_FEATURES)
            ?.split(REGEX_SEQUENCE_SEPARATOR)?.filterTo(mutableSetOf()) { it.isNotBlank() }.orEmpty()

        invisibleIf = parser.getAttributeValue(XML_INVISIBLE_IF).toExpressionOrNull()
        goneIf = parser.getAttributeValue(XML_GONE_IF).toExpressionOrNull()
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        parent: Base = Manifest(),
        version: Int = SCHEMA_VERSION,
        restrictTo: Set<DeviceType> = DeviceType.ALL,
        requiredFeatures: Set<String> = emptySet(),
        invisibleIf: Expression? = null,
        goneIf: Expression? = null
    ) : super(parent) {
        this.restrictTo = restrictTo
        this.version = version
        this.requiredFeatures = requiredFeatures
        this.invisibleIf = invisibleIf
        this.goneIf = goneIf
    }

    /**
     * @return true if this content element should be completely ignored.
     */
    internal open val isIgnored
        get() = version > SCHEMA_VERSION ||
            !ParserConfig.supportedFeatures.containsAll(requiredFeatures) ||
            restrictTo.none { it in DeviceType.SUPPORTED } ||
            invisibleIf?.isValid() == false ||
            goneIf?.isValid() == false

    fun isInvisible(state: State) = invisibleIf?.evaluate(state) ?: false
    fun isInvisibleFlow(state: State) = state.changeFlow(invisibleIf?.vars()) { isInvisible(it) }
    fun isGone(state: State) = goneIf?.evaluate(state) ?: false
    fun isGoneFlow(state: State) = state.changeFlow(goneIf?.vars()) { isGone(it) }

    open val tips get() = emptyList<Tip>()

    internal companion object {
        internal fun XmlPullParser.parseContentElement(parent: Base): Content? {
            require(XmlPullParser.START_TAG, null, null)
            return when (namespace) {
                XMLNS_CONTENT -> when (name) {
                    Accordion.XML_ACCORDION -> Accordion(parent, this)
                    Animation.XML_ANIMATION -> Animation(parent, this)
                    Button.XML_BUTTON -> Button(parent, this)
                    Fallback.XML_FALLBACK -> Fallback(parent, this).content.firstOrNull()
                    Form.XML_FORM -> Form(parent, this)
                    Image.XML_IMAGE -> Image(parent, this)
                    Input.XML_INPUT -> Input(parent, this)
                    Link.XML_LINK -> Link(parent, this)
                    Multiselect.XML_MULTISELECT -> Multiselect(parent, this)
                    Paragraph.XML_PARAGRAPH ->
                        when (getAttributeValue(Paragraph.XML_FALLBACK)?.toBoolean()) {
                            true -> Fallback(parent, this).content.firstOrNull()
                            else -> Paragraph(parent, this)
                        }
                    Spacer.XML_SPACER -> Spacer(parent, this)
                    Tabs.XML_TABS -> Tabs(parent, this)
                    Text.XML_TEXT -> Text(parent, this)
                    Video.XML_VIDEO -> Video(parent, this)
                    else -> null
                }
                XMLNS_TRAINING -> when (name) {
                    InlineTip.XML_TIP -> InlineTip(parent, this)
                    else -> null
                }
                else -> null
            }
        }
    }
}

@RestrictTo(RestrictTo.Scope.TESTS)
internal val Content.testIsIgnored get() = isIgnored
