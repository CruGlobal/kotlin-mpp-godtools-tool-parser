@file:JvmMultifileClass
@file:JvmName("PageKt")

package org.cru.godtools.shared.tool.parser.model.page

import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import co.touchlab.kermit.Logger
import com.github.ajalt.colormath.Color
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.native.HiddenFromObjC
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_PAGE_COLLECTION
import org.cru.godtools.shared.tool.parser.internal.color
import org.cru.godtools.shared.tool.parser.internal.toColorOrNull
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.model.Background
import org.cru.godtools.shared.tool.parser.model.BaseModel
import org.cru.godtools.shared.tool.parser.model.Card
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Gravity.Companion.toGravityOrNull
import org.cru.godtools.shared.tool.parser.model.HasAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.HasPages
import org.cru.godtools.shared.tool.parser.model.ImageScaleType
import org.cru.godtools.shared.tool.parser.model.ImageScaleType.Companion.toImageScaleTypeOrNull
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Multiselect.Companion.XML_MULTISELECT_OPTION_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.Multiselect.Companion.XML_MULTISELECT_OPTION_SELECTED_COLOR
import org.cru.godtools.shared.tool.parser.model.Styles
import org.cru.godtools.shared.tool.parser.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.shared.tool.parser.model.XMLNS_CONTENT
import org.cru.godtools.shared.tool.parser.model.XMLNS_CYOA
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_IMAGE
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.shared.tool.parser.model.XML_DISMISS_LISTENERS
import org.cru.godtools.shared.tool.parser.model.XML_LISTENERS
import org.cru.godtools.shared.tool.parser.model.XML_PRIMARY_COLOR
import org.cru.godtools.shared.tool.parser.model.XML_PRIMARY_TEXT_COLOR
import org.cru.godtools.shared.tool.parser.model.XML_TEXT_COLOR
import org.cru.godtools.shared.tool.parser.model.XML_TEXT_SCALE
import org.cru.godtools.shared.tool.parser.model.getResource
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.cru.godtools.shared.tool.parser.model.lesson.XMLNS_LESSON
import org.cru.godtools.shared.tool.parser.model.page.CardCollectionPage.Companion.TYPE_CARD_COLLECTION
import org.cru.godtools.shared.tool.parser.model.page.ContentPage.Companion.TYPE_CONTENT
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.shared.tool.parser.model.page.PageCollectionPage.Companion.TYPE_PAGE_COLLECTION
import org.cru.godtools.shared.tool.parser.model.primaryColor
import org.cru.godtools.shared.tool.parser.model.primaryTextColor
import org.cru.godtools.shared.tool.parser.model.stylesParent
import org.cru.godtools.shared.tool.parser.model.textColor
import org.cru.godtools.shared.tool.parser.model.textScale
import org.cru.godtools.shared.tool.parser.model.toEventIds
import org.cru.godtools.shared.tool.parser.model.toPlatformColor
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.cru.godtools.shared.tool.parser.model.tract.XMLNS_TRACT
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserException

private const val XML_TYPE = "type"
private const val XML_ID = "id"
private const val XML_PARENT = "parent"
private const val XML_HIDDEN = "hidden"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
abstract class Page : BaseModel, Styles, HasAnalyticsEvents {
    internal companion object {
        internal const val XML_PAGE = "page"

        private const val XML_PARENT_PAGE_COLLECTION_OVERRIDE = "parent_override_page-collection"

        @VisibleForTesting
        internal val DEFAULT_BACKGROUND_COLOR = color(0, 0, 0, 0.0)
        @VisibleForTesting
        internal val DEFAULT_BACKGROUND_IMAGE_GRAVITY = Gravity.CENTER
        @VisibleForTesting
        internal val DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE = ImageScaleType.FILL_X

        internal suspend fun parse(
            container: HasPages,
            fileName: String?,
            parser: XmlPullParser,
            parseFile: suspend (String) -> XmlPullParser,
        ): Page? {
            parser.require(XmlPullParser.START_TAG, null, XML_PAGE)

            return when (parser.namespace to parser.getAttributeValue(XMLNS_XSI, XML_TYPE)) {
                XMLNS_PAGE to TYPE_PAGE_COLLECTION -> {
                    if (!container.supportsPageType(PageCollectionPage::class)) return null
                    PageCollectionPage.parse(container, fileName, parser, parseFile)
                }
                else -> parse(container, fileName, parser)
            }?.takeIf { container.supportsPageType(it::class) }
        }

        internal fun parse(container: HasPages, fileName: String?, parser: XmlPullParser): Page? {
            parser.require(XmlPullParser.START_TAG, null, XML_PAGE)

            @Suppress("ktlint:standard:blank-line-between-when-conditions")
            return when (parser.namespace) {
                XMLNS_LESSON -> LessonPage(container, fileName, parser)
                XMLNS_TRACT -> TractPage(container, fileName, parser)
                XMLNS_PAGE -> when (val type = parser.getAttributeValue(XMLNS_XSI, XML_TYPE)) {
                    TYPE_CARD_COLLECTION -> CardCollectionPage(container, fileName, parser)
                    TYPE_CONTENT -> ContentPage(container, fileName, parser)
                    else -> {
                        val message = "Unrecognized page type: <${parser.namespace}:${parser.name} type=$type>"
                        Logger.e(message, UnsupportedOperationException(message), "Page")
                        null
                    }
                }
                else -> {
                    val message = "Unrecognized page namespace: ${parser.namespace}"
                    Logger.e(message, UnsupportedOperationException(message), "Page")
                    null
                }
            }?.takeIf { container.supportsPageType(it::class) }
        }

        internal fun XmlPullParser.requirePageType(type: String) {
            val actual = getAttributeValue(XMLNS_XSI, XML_TYPE)
            if (type != actual) throw XmlPullParserException("expected $type not $actual")
        }
    }

    private val parentPageContainer: HasPages
        get() {
            var parent = parent
            while (parent !is HasPages) parent = parent?.parent ?: return manifest
            return parent
        }

    val id by lazy { _id ?: fileName ?: "${manifest.code}-$position" }
    val position by lazy { parentPageContainer.pages.indexOf(this) }

    private val _id: String?
    @VisibleForTesting
    internal val fileName: String?

    private val _parentPage: String?
    val parentPage get() = parentPageContainer.findPage(_parentPage?.substringBefore("?"))
    val parentPageParams get() = when {
        parentPage == null -> emptyMap()
        else -> _parentPage.orEmpty().substringAfter("?", "")
            .split("&")
            .mapNotNull { it.split("=", limit = 2).takeIf { it.size == 2 } }
            .associate { (key, value) -> key to value }
    }

    val nextPage get() = parentPageContainer.pages.getOrNull(position + 1)
    val previousPage get() = parentPageContainer.pages.getOrNull(position - 1)

    val isHidden: Boolean

    @JsExport.Ignore
    @JsName("_listeners")
    val listeners: Set<EventId>
    @JsExport.Ignore
    @JsName("_dismissListeners")
    val dismissListeners: Set<EventId>

    private val _primaryColor: Color?
    override val primaryColor get() = _primaryColor ?: stylesParent.primaryColor

    private val _primaryTextColor: Color?
    override val primaryTextColor get() = _primaryTextColor ?: stylesParent.primaryTextColor

    val background by lazy {
        Background(
            backgroundColor,
            backgroundImage,
            backgroundImageGravity,
            backgroundImageScaleType,
        )
    }

    val backgroundColor: Color

    private val _backgroundImage: String?
    val backgroundImage get() = getResource(_backgroundImage)
    val backgroundImageGravity: Gravity
    val backgroundImageScaleType: ImageScaleType

    private val _controlColor: Color?
    @JsName("_controlColor")
    @JsExport.Ignore
    val controlColor: Color
        get() = _controlColor ?: (parentPageContainer as? Page)?.controlColor ?: manifest.pageControlColor

    private val _cardBackgroundColor: Color?
    override val cardBackgroundColor get() = _cardBackgroundColor ?: super.cardBackgroundColor

    private val _multiselectOptionBackgroundColor: Color?
    override val multiselectOptionBackgroundColor
        get() = _multiselectOptionBackgroundColor ?: super.multiselectOptionBackgroundColor
    private val _multiselectOptionSelectedColor: Color?
    override val multiselectOptionSelectedColor
        get() = _multiselectOptionSelectedColor ?: super.multiselectOptionSelectedColor

    private val _textColor: Color?
    override val textColor get() = _textColor ?: stylesParent.textColor
    private val _textScale: Double
    override val textScale get() = _textScale * stylesParent.textScale

    internal constructor(container: HasPages, fileName: String?, parser: XmlPullParser) : super(container) {
        parser.require(XmlPullParser.START_TAG, null, XML_PAGE)

        _id = parser.getAttributeValue(XML_ID)
        this.fileName = fileName
        _parentPage =
            parser.getAttributeValue(XMLNS_CYOA, XML_PARENT_PAGE_COLLECTION_OVERRIDE)
                ?.takeIf { manifest.config.supportsFeature(FEATURE_PAGE_COLLECTION) }
                ?: parser.getAttributeValue(XMLNS_CYOA, XML_PARENT)

        isHidden = parser.getAttributeValue(XML_HIDDEN)?.toBoolean() ?: false

        listeners = parser.getAttributeValue(XML_LISTENERS)?.toEventIds()?.toSet().orEmpty()
        dismissListeners = parser.getAttributeValue(XML_DISMISS_LISTENERS)?.toEventIds()?.toSet().orEmpty()

        _primaryColor = parser.getAttributeValue(XML_PRIMARY_COLOR)?.toColorOrNull()
        _primaryTextColor = parser.getAttributeValue(XML_PRIMARY_TEXT_COLOR)?.toColorOrNull()

        backgroundColor = parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull() ?: DEFAULT_BACKGROUND_COLOR
        _backgroundImage = parser.getAttributeValue(XML_BACKGROUND_IMAGE)
        backgroundImageGravity = parser.getAttributeValue(XML_BACKGROUND_IMAGE_GRAVITY)?.toGravityOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY
        backgroundImageScaleType = parser.getAttributeValue(XML_BACKGROUND_IMAGE_SCALE_TYPE)?.toImageScaleTypeOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

        _controlColor = parser.getAttributeValue(XML_CONTROL_COLOR)?.toColorOrNull()

        _cardBackgroundColor = parser.getAttributeValue(XMLNS_CONTENT, Card.XML_CARD_BACKGROUND_COLOR)?.toColorOrNull()

        _multiselectOptionBackgroundColor =
            parser.getAttributeValue(XMLNS_CONTENT, XML_MULTISELECT_OPTION_BACKGROUND_COLOR)?.toColorOrNull()
        _multiselectOptionSelectedColor =
            parser.getAttributeValue(XMLNS_CONTENT, XML_MULTISELECT_OPTION_SELECTED_COLOR)?.toColorOrNull()

        _textColor = parser.getAttributeValue(XML_TEXT_COLOR)?.toColorOrNull()
        _textScale = parser.getAttributeValue(XML_TEXT_SCALE)?.toDoubleOrNull() ?: DEFAULT_TEXT_SCALE
    }

    @RestrictTo(RestrictTo.Scope.SUBCLASSES, RestrictTo.Scope.TESTS)
    internal constructor(
        container: HasPages = Manifest(),
        id: String? = null,
        fileName: String? = null,
        parentPage: String? = null,
        primaryColor: Color? = null,
        backgroundColor: Color = DEFAULT_BACKGROUND_COLOR,
        backgroundImage: String? = null,
        backgroundImageGravity: Gravity = DEFAULT_BACKGROUND_IMAGE_GRAVITY,
        backgroundImageScaleType: ImageScaleType = DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE,
        controlColor: Color? = null,
        cardBackgroundColor: Color? = null,
        multiselectOptionBackgroundColor: Color? = null,
        multiselectOptionSelectedColor: Color? = null,
        textColor: Color? = null,
        textScale: Double = DEFAULT_TEXT_SCALE
    ) : super(container) {
        _id = id
        this.fileName = fileName
        _parentPage = parentPage

        isHidden = false

        listeners = emptySet()
        dismissListeners = emptySet()

        _primaryColor = primaryColor
        _primaryTextColor = null

        this.backgroundColor = backgroundColor
        _backgroundImage = backgroundImage
        this.backgroundImageGravity = backgroundImageGravity
        this.backgroundImageScaleType = backgroundImageScaleType

        _controlColor = controlColor

        _cardBackgroundColor = cardBackgroundColor

        _multiselectOptionBackgroundColor = multiselectOptionBackgroundColor
        _multiselectOptionSelectedColor = multiselectOptionSelectedColor

        _textColor = textColor
        _textScale = textScale
    }

    // region HasAnalyticsEvents
    @VisibleForTesting
    internal abstract val analyticsEvents: List<AnalyticsEvent>

    override fun getAnalyticsEvents(type: Trigger) = when (type) {
        Trigger.VISIBLE -> analyticsEvents.filter { it.isTriggerType(Trigger.VISIBLE, Trigger.DEFAULT) }
        Trigger.HIDDEN -> analyticsEvents.filter { it.isTriggerType(Trigger.HIDDEN) }
        else -> error("Analytics trigger type $type is not currently supported on Pages")
    }
    // endregion HasAnalyticsEvents

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("dismissListeners")
    val jsDismissListeners get() = dismissListeners.toTypedArray()

    @HiddenFromObjC
    @JsName("listeners")
    val jsListeners get() = listeners.toTypedArray()

    @HiddenFromObjC
    @JsName("controlColor")
    val platformControlColor get() = controlColor.toPlatformColor()
    // endregion Kotlin/JS interop
}
