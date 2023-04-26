package org.cru.godtools.shared.tool.parser.model

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.ccci.gto.support.androidx.annotation.VisibleForTesting
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_REQUIRED_VERSIONS
import org.cru.godtools.shared.tool.parser.expressions.Expression
import org.cru.godtools.shared.tool.parser.model.DeviceType.Companion.toDeviceTypes
import org.cru.godtools.shared.tool.parser.model.Version.Companion.toVersionOrNull
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.parser.model.tips.XMLNS_TRAINING
import org.cru.godtools.shared.tool.parser.util.REGEX_SEQUENCE_SEPARATOR
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.native.HiddenFromObjC

private const val XML_REQUIRED_FEATURES = "required-features"
private const val XML_REQUIRED_DEVICE_TYPE = "required-device-type"
private const val XML_REQUIRED_ANDROID_VERSION = "required-android-version"
private const val XML_REQUIRED_IOS_VERSION = "required-ios-version"
private const val XML_RESTRICT_TO = "restrictTo"
private const val XML_VERSION = "version"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
abstract class Content : BaseModel, Visibility {
    @HiddenFromObjC
    val contentType: String
    private val version: Int
    private val requiredFeatures: Set<String>
    @VisibleForTesting
    internal val requiredDeviceType: Set<DeviceType>
    @VisibleForTesting
    internal val requiredAndroidVersion: Version?
    @VisibleForTesting
    internal val requiredIosVersion: Version?

    final override val invisibleIf: Expression?
    final override val goneIf: Expression?

    internal constructor(parent: Base, type: String, parser: XmlPullParser) : super(parent) {
        contentType = type
        version = parser.getAttributeValue(null, XML_VERSION)?.toIntOrNull() ?: SCHEMA_VERSION
        requiredFeatures = parser.getAttributeValue(XML_REQUIRED_FEATURES)
            ?.split(REGEX_SEQUENCE_SEPARATOR)?.filterTo(mutableSetOf()) { it.isNotBlank() }.orEmpty()
        requiredDeviceType = parser.getAttributeValue(XML_REQUIRED_DEVICE_TYPE)?.toDeviceTypes()
            ?: parser.getAttributeValue(XML_RESTRICT_TO)?.toDeviceTypes()
            ?: DeviceType.ALL
        requiredAndroidVersion = parser.getAttributeValue(XML_REQUIRED_ANDROID_VERSION)
            ?.let { it.toVersionOrNull() ?: Version.MAX }
        requiredIosVersion = parser.getAttributeValue(XML_REQUIRED_IOS_VERSION)
            ?.let { it.toVersionOrNull() ?: Version.MAX }

        parser.parseVisibilityAttrs { invisibleIf, goneIf ->
            this.invisibleIf = invisibleIf
            this.goneIf = goneIf
        }
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(
        parent: Base = Manifest(),
        contentType: String = "",
        version: Int = SCHEMA_VERSION,
        requiredFeatures: Set<String> = emptySet(),
        requiredDeviceType: Set<DeviceType> = DeviceType.ALL,
        requiredAndroidVersion: Version? = null,
        requiredIosVersion: Version? = null,
        invisibleIf: Expression? = null,
        goneIf: Expression? = null
    ) : super(parent) {
        this.contentType = contentType
        this.requiredDeviceType = requiredDeviceType
        this.version = version
        this.requiredFeatures = requiredFeatures
        this.requiredAndroidVersion = requiredAndroidVersion
        this.requiredIosVersion = requiredIosVersion
        this.invisibleIf = invisibleIf
        this.goneIf = goneIf
    }

    /**
     * @return true if this content element should be completely ignored.
     */
    internal open val isIgnored
        get() = version > SCHEMA_VERSION ||
            !areContentRestrictionsSatisfied ||
            requiredFeatures.any { !manifest.config.supportsFeature(it) } ||
            invisibleIf?.isValid() == false ||
            goneIf?.isValid() == false

    private val areContentRestrictionsSatisfied
        get() = requiredDeviceType.any { manifest.config.supportsDeviceType(it) } &&
            manifest.config.isRequiredVersionSatisfied(DeviceType.ANDROID, requiredAndroidVersion) &&
            manifest.config.isRequiredVersionSatisfied(DeviceType.IOS, requiredIosVersion)

    open val tips get() = emptyList<Tip>()

    internal companion object {
        internal fun XmlPullParser.parseContentElement(parent: Base): Content? {
            require(XmlPullParser.START_TAG, null, null)
            return when (namespace) {
                XMLNS_CONTENT -> when (name) {
                    Accordion.XML_ACCORDION -> Accordion(parent, this)
                    Animation.XML_ANIMATION -> Animation(parent, this)
                    Button.XML_BUTTON -> Button(parent, this)
                    Card.XML_CARD -> Card(parent, this)
                    Fallback.XML_FALLBACK -> Fallback(parent, this).content.firstOrNull()
                    Flow.XML_FLOW -> Flow(parent, this)
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

private fun ParserConfig.isRequiredVersionSatisfied(deviceType: DeviceType, version: Version?) = when {
    version == null -> true
    !supportsFeature(FEATURE_REQUIRED_VERSIONS) -> false
    deviceType != this.deviceType -> true
    (appVersion ?: Version.MIN) >= version -> true
    else -> false
}
