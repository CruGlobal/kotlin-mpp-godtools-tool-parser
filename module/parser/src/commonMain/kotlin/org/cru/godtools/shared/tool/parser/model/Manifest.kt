package org.cru.godtools.shared.tool.parser.model

import co.touchlab.kermit.Logger
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC
import kotlin.reflect.KClass
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.ccci.gto.support.androidx.annotation.VisibleForTesting
import org.ccci.gto.support.fluidsonic.locale.PlatformLocale
import org.cru.godtools.shared.common.model.Uri
import org.cru.godtools.shared.common.model.isHttpUrl
import org.cru.godtools.shared.common.model.toUriOrNull
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_PAGE_COLLECTION
import org.cru.godtools.shared.tool.parser.internal.AndroidColorInt
import org.cru.godtools.shared.tool.parser.internal.DeprecationException
import org.cru.godtools.shared.tool.parser.internal.fluidlocale.toLocaleOrNull
import org.cru.godtools.shared.tool.parser.model.Gravity.Companion.toGravityOrNull
import org.cru.godtools.shared.tool.parser.model.ImageScaleType.Companion.toImageScaleTypeOrNull
import org.cru.godtools.shared.tool.parser.model.Multiselect.Companion.XML_MULTISELECT_OPTION_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.Multiselect.Companion.XML_MULTISELECT_OPTION_SELECTED_COLOR
import org.cru.godtools.shared.tool.parser.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.shared.tool.parser.model.lesson.DEFAULT_LESSON_NAV_BAR_COLOR
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.cru.godtools.shared.tool.parser.model.lesson.XMLNS_LESSON
import org.cru.godtools.shared.tool.parser.model.page.CardCollectionPage
import org.cru.godtools.shared.tool.parser.model.page.ContentPage
import org.cru.godtools.shared.tool.parser.model.page.DEFAULT_CONTROL_COLOR
import org.cru.godtools.shared.tool.parser.model.page.Page
import org.cru.godtools.shared.tool.parser.model.page.PageCollectionPage
import org.cru.godtools.shared.tool.parser.model.page.XMLNS_PAGE
import org.cru.godtools.shared.tool.parser.model.page.XML_CONTROL_COLOR
import org.cru.godtools.shared.tool.parser.model.shareable.Shareable
import org.cru.godtools.shared.tool.parser.model.shareable.Shareable.Companion.parseShareableItems
import org.cru.godtools.shared.tool.parser.model.shareable.XMLNS_SHAREABLE
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.cru.godtools.shared.tool.parser.util.setOnce
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_MANIFEST = "manifest"
private const val XML_TOOL = "tool"
private const val XML_LOCALE = "locale"
private const val XML_TYPE = "type"
private const val XML_TYPE_ARTICLE = "article"
private const val XML_TYPE_CYOA = "cyoa"
private const val XML_TYPE_LESSON = "lesson"
private const val XML_TYPE_TRACT = "tract"
private const val XML_NAVBAR_COLOR = "navbar-color"
private const val XML_NAVBAR_CONTROL_COLOR = "navbar-control-color"
private const val XML_CATEGORY_LABEL_COLOR = "category-label-color"
private const val XML_TITLE = "title"
private const val XML_CATEGORIES = "categories"
private const val XML_PAGES = "pages"
private const val XML_PAGES_PAGE = "page"
private const val XML_PAGES_PAGE_FILENAME = "filename"
private const val XML_PAGES_PAGE_SRC = "src"
private const val XML_PAGES_AEM_IMPORT = "aem-import"
private const val XML_PAGES_AEM_IMPORT_SRC = "src"
private const val XML_RESOURCES = "resources"
private const val XML_TIPS = "tips"
private const val XML_TIPS_TIP = "tip"
private const val XML_TIPS_TIP_ID = "id"
private const val XML_TIPS_TIP_SRC = "src"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class Manifest : BaseModel, Styles, HasPages {
    internal companion object {
        @AndroidColorInt
        internal val DEFAULT_PRIMARY_COLOR = color(59, 164, 219, 1.0)
        @AndroidColorInt
        internal val DEFAULT_PRIMARY_TEXT_COLOR = color(255, 255, 255, 1.0)

        @AndroidColorInt
        internal val DEFAULT_BACKGROUND_COLOR = color(255, 255, 255, 1.0)
        internal val DEFAULT_BACKGROUND_IMAGE_GRAVITY = Gravity.CENTER
        internal val DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE = ImageScaleType.FILL

        internal val DEFAULT_BUTTON_STYLE = Button.Style.CONTAINED

        @AndroidColorInt
        internal val DEFAULT_TEXT_COLOR = color(90, 90, 90, 1.0)

        internal suspend fun parse(
            fileName: String,
            config: ParserConfig,
            parseFile: suspend (String) -> XmlPullParser
        ): Manifest {
            val manifest = Manifest(parseFile(fileName), config)
            coroutineScope {
                // parse pages
                if (config.parsePages) {
                    launch {
                        manifest.pages = manifest.pageXmlFiles
                            .map { (name, src) -> async { Page.parse(manifest, name, parseFile(src)) } }
                            .awaitAll().filterNotNull()
                    }
                } else {
                    manifest.pages = emptyList()
                }

                // parse tips
                if (config.parseTips) {
                    launch {
                        manifest.tips = manifest.tipXmlFiles
                            .map { (id, src) -> async { Tip(manifest, id.orEmpty(), parseFile(src)) } }
                            .awaitAll()
                            .associateBy { it.id }
                    }
                } else {
                    manifest.tips = emptyMap()
                }
            }
            return manifest
        }
    }

    internal val config: ParserConfig

    val code: String?
    val locale: PlatformLocale?
    val type: Type

    @JsName("_dismissListeners")
    val dismissListeners: Set<EventId>

    @AndroidColorInt
    override val primaryColor: PlatformColor
    @AndroidColorInt
    override val primaryTextColor: PlatformColor

    @AndroidColorInt
    @Suppress("ktlint:standard:property-naming") // https://github.com/pinterest/ktlint/issues/2448
    private val _navBarColor: PlatformColor?
    @get:AndroidColorInt
    internal val navBarColor
        get() = _navBarColor ?: if (type == Type.LESSON) DEFAULT_LESSON_NAV_BAR_COLOR else primaryColor
    @AndroidColorInt
    @Suppress("ktlint:standard:property-naming") // https://github.com/pinterest/ktlint/issues/2448
    private val _navBarControlColor: PlatformColor?
    @get:AndroidColorInt
    internal val navBarControlColor
        get() = _navBarControlColor ?: if (type == Type.LESSON) primaryColor else primaryTextColor

    @AndroidColorInt
    internal val backgroundColor: PlatformColor
    private val _backgroundImage: String?
    val backgroundImage get() = getResource(_backgroundImage)
    internal val backgroundImageGravity: Gravity
    internal val backgroundImageScaleType: ImageScaleType

    @AndroidColorInt
    private val _cardBackgroundColor: PlatformColor?
    @get:AndroidColorInt
    override val cardBackgroundColor get() = _cardBackgroundColor ?: backgroundColor

    @AndroidColorInt
    @Suppress("ktlint:standard:property-naming") // https://github.com/pinterest/ktlint/issues/2448
    private val _categoryLabelColor: PlatformColor?
    @get:AndroidColorInt
    internal val categoryLabelColor get() = _categoryLabelColor ?: textColor

    @AndroidColorInt
    internal val pageControlColor: PlatformColor

    override val buttonStyle get() = DEFAULT_BUTTON_STYLE

    private val _multiselectOptionBackgroundColor: PlatformColor?
    override val multiselectOptionBackgroundColor
        get() = _multiselectOptionBackgroundColor ?: super.multiselectOptionBackgroundColor
    override val multiselectOptionSelectedColor: PlatformColor?

    @AndroidColorInt
    override val textColor: PlatformColor
    override val textScale: Double

    private val _title: Text?
    val title: String? get() = _title?.text

    val aemImports: List<Uri>
    val categories: List<Category>
    override var pages: List<Page> by setOnce()
        private set
    @VisibleForTesting
    internal val resources: Map<String?, Resource>
    val shareables: List<Shareable>
    @VisibleForTesting
    internal var tips: Map<String, Tip> by setOnce()
        private set

    private val pageXmlFiles: List<XmlFile>
    private val tipXmlFiles: List<XmlFile>

    val relatedFiles get() = buildSet {
        addAll(pageXmlFiles.map { it.src })
        addAll(tipXmlFiles.map { it.src })
        addAll(resources.values.mapNotNull { it.localName })
    }

    private constructor(parser: XmlPullParser, config: ParserConfig) {
        parser.require(XmlPullParser.START_TAG, XMLNS_MANIFEST, XML_MANIFEST)

        this.config = config

        code = parser.getAttributeValue(XML_TOOL)
        locale = parser.getAttributeValue(XML_LOCALE)?.toLocaleOrNull()
        type = Type.parseOrNull(parser.getAttributeValue(XML_TYPE)) ?: Type.DEFAULT

        dismissListeners = parser.getAttributeValue(XML_DISMISS_LISTENERS).toEventIds().toSet()

        primaryColor = parser.getAttributeValue(XML_PRIMARY_COLOR)?.toColorOrNull() ?: DEFAULT_PRIMARY_COLOR
        primaryTextColor =
            parser.getAttributeValue(XML_PRIMARY_TEXT_COLOR)?.toColorOrNull() ?: DEFAULT_PRIMARY_TEXT_COLOR

        _navBarColor = parser.getAttributeValue(XML_NAVBAR_COLOR)?.toColorOrNull()
        _navBarControlColor = parser.getAttributeValue(XML_NAVBAR_CONTROL_COLOR)?.toColorOrNull()

        backgroundColor = parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull() ?: DEFAULT_BACKGROUND_COLOR
        _backgroundImage = parser.getAttributeValue(null, XML_BACKGROUND_IMAGE)
        backgroundImageGravity = parser.getAttributeValue(XML_BACKGROUND_IMAGE_GRAVITY)?.toGravityOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY
        backgroundImageScaleType = parser.getAttributeValue(XML_BACKGROUND_IMAGE_SCALE_TYPE)?.toImageScaleTypeOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

        _cardBackgroundColor = parser.getAttributeValue(XMLNS_CONTENT, Card.XML_CARD_BACKGROUND_COLOR)?.toColorOrNull()
        _categoryLabelColor = parser.getAttributeValue(XML_CATEGORY_LABEL_COLOR)?.toColorOrNull()
        pageControlColor =
            parser.getAttributeValue(XMLNS_PAGE, XML_CONTROL_COLOR)?.toColorOrNull()
                ?: parser.getAttributeValue(XMLNS_LESSON, XML_CONTROL_COLOR)?.toColorOrNull()?.also {
                    val message = "Deprecated lesson:control-color defined on tool: $code language: $locale"
                    Logger.e(message, DeprecationException(message), "Manifest")
                }
                ?: DEFAULT_CONTROL_COLOR

        _multiselectOptionBackgroundColor =
            parser.getAttributeValue(XMLNS_CONTENT, XML_MULTISELECT_OPTION_BACKGROUND_COLOR)?.toColorOrNull()
        multiselectOptionSelectedColor =
            parser.getAttributeValue(XMLNS_CONTENT, XML_MULTISELECT_OPTION_SELECTED_COLOR)?.toColorOrNull()

        textColor = parser.getAttributeValue(XML_TEXT_COLOR)?.toColorOrNull() ?: DEFAULT_TEXT_COLOR
        textScale = parser.getAttributeValue(XML_TEXT_SCALE)?.toDoubleOrNull() ?: DEFAULT_TEXT_SCALE

        var title: Text? = null
        aemImports = mutableListOf()
        categories = mutableListOf()
        resources = mutableMapOf()
        val shareables = mutableListOf<Shareable>()
        pageXmlFiles = mutableListOf()
        tipXmlFiles = mutableListOf()
        parser.parseChildren {
            @Suppress("ktlint:standard:blank-line-between-when-conditions")
            when (parser.namespace) {
                XMLNS_MANIFEST -> when (parser.name) {
                    XML_TITLE -> title = parser.parseTextChild(this, XMLNS_MANIFEST, XML_TITLE)
                    XML_CATEGORIES -> categories += parser.parseCategories()
                    XML_PAGES -> {
                        val result = parser.parsePages()
                        aemImports += result.aemImports
                        pageXmlFiles += result.pages
                    }
                    XML_RESOURCES -> resources += parser.parseResources().associateBy { it.name }
                    XML_TIPS -> tipXmlFiles += parser.parseTips()
                }

                XMLNS_SHAREABLE -> when (parser.name) {
                    Shareable.XML_ITEMS -> shareables += parser.parseShareableItems(this)
                }
            }
        }
        this.shareables = shareables.distinctBy { it.id }.sortedBy { it.order }

        _title = title
    }

    @JsName("createTestManifest")
    @RestrictTo(RestrictToScope.TESTS)
    constructor(
        config: ParserConfig = ParserConfig(),
        type: Type = Type.DEFAULT,
        code: String? = null,
        locale: PlatformLocale? = null,
        primaryColor: PlatformColor = DEFAULT_PRIMARY_COLOR,
        primaryTextColor: PlatformColor = DEFAULT_PRIMARY_TEXT_COLOR,
        navBarColor: PlatformColor? = null,
        navBarControlColor: PlatformColor? = null,
        backgroundColor: PlatformColor = DEFAULT_BACKGROUND_COLOR,
        cardBackgroundColor: PlatformColor? = null,
        categoryLabelColor: PlatformColor? = null,
        pageControlColor: PlatformColor = DEFAULT_CONTROL_COLOR,
        multiselectOptionBackgroundColor: PlatformColor? = null,
        multiselectOptionSelectedColor: PlatformColor? = null,
        textColor: PlatformColor = DEFAULT_TEXT_COLOR,
        textScale: Double = DEFAULT_TEXT_SCALE,
        resources: ((Manifest) -> List<Resource>)? = null,
        shareables: ((Manifest) -> List<Shareable>)? = null,
        tips: ((Manifest) -> List<Tip>)? = null,
        pages: ((Manifest) -> List<Page>)? = null
    ) {
        this.config = config

        this.code = code
        this.locale = locale
        this.type = type

        dismissListeners = emptySet()

        this.primaryColor = primaryColor
        this.primaryTextColor = primaryTextColor

        _navBarColor = navBarColor
        _navBarControlColor = navBarControlColor

        this.backgroundColor = backgroundColor
        _backgroundImage = null
        backgroundImageGravity = DEFAULT_BACKGROUND_IMAGE_GRAVITY
        backgroundImageScaleType = DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

        _cardBackgroundColor = cardBackgroundColor
        _categoryLabelColor = categoryLabelColor
        this.pageControlColor = pageControlColor

        _multiselectOptionBackgroundColor = multiselectOptionBackgroundColor
        this.multiselectOptionSelectedColor = multiselectOptionSelectedColor

        this.textColor = textColor
        this.textScale = textScale

        _title = null

        aemImports = emptyList()
        categories = emptyList()
        this.pages = pages?.invoke(this).orEmpty()
        this.resources = resources?.invoke(this)?.associateBy { it.name }.orEmpty()
        this.shareables = shareables?.invoke(this).orEmpty()
        this.tips = tips?.invoke(this)?.associateBy { it.id }.orEmpty()

        pageXmlFiles = emptyList()
        tipXmlFiles = emptyList()
    }

    override val manifest get() = this
    val hasTips get() = tips.isNotEmpty() || (!config.parseTips && tipXmlFiles.isNotEmpty())
    internal fun getResource(name: String?) = name?.let { resources[name] }

    @JsExport.Ignore
    fun findCategory(category: String?) = categories.firstOrNull { it.id == category }
    @JsExport.Ignore
    fun findShareable(id: String?) = id?.let { shareables.firstOrNull { it.id == id } }
    @JsExport.Ignore
    fun findTip(id: String?) = tips[id]

    override fun <T : Page> supportsPageType(type: KClass<T>) = when (this.type) {
        Type.ARTICLE -> false
        Type.CYOA -> when (type) {
            CardCollectionPage::class,
            ContentPage::class -> true
            PageCollectionPage::class -> config.supportsFeature(FEATURE_PAGE_COLLECTION)
            else -> false
        }
        Type.LESSON -> type == LessonPage::class
        Type.TRACT -> type == TractPage::class
        Type.UNKNOWN -> false
    }

    private fun XmlPullParser.parseCategories() = buildList {
        require(XmlPullParser.START_TAG, XMLNS_MANIFEST, XML_CATEGORIES)
        parseChildren {
            when (namespace) {
                XMLNS_MANIFEST -> when (name) {
                    Category.XML_CATEGORY -> add(Category(this@Manifest, this@parseCategories))
                }
            }
        }
    }

    private class PagesData {
        val aemImports = mutableListOf<Uri>()
        val pages = mutableListOf<XmlFile>()
    }

    private fun XmlPullParser.parsePages() = PagesData().also { result ->
        require(XmlPullParser.START_TAG, XMLNS_MANIFEST, XML_PAGES)

        // process any child elements
        parseChildren {
            when (namespace) {
                XMLNS_MANIFEST -> when (name) {
                    XML_PAGES_PAGE -> {
                        val src = getAttributeValue(XML_PAGES_PAGE_SRC) ?: return@parseChildren
                        val fileName = getAttributeValue(XML_PAGES_PAGE_FILENAME)
                        result.pages += XmlFile(fileName, src)
                    }
                }

                XMLNS_ARTICLE -> when (name) {
                    XML_PAGES_AEM_IMPORT -> getAttributeValue(XML_PAGES_AEM_IMPORT_SRC)?.toUriOrNull()
                        ?.takeIf { it.isHttpUrl }
                        ?.let { result.aemImports += it }
                }
            }
        }
    }

    private fun XmlPullParser.parseResources() = buildList {
        require(XmlPullParser.START_TAG, XMLNS_MANIFEST, XML_RESOURCES)
        parseChildren {
            when (namespace) {
                XMLNS_MANIFEST -> when (name) {
                    Resource.XML_RESOURCE -> add(Resource(this@Manifest, this@parseResources))
                }
            }
        }
    }

    private fun XmlPullParser.parseTips() = buildList {
        parseChildren {
            when (namespace) {
                XMLNS_MANIFEST -> when (name) {
                    XML_TIPS_TIP -> {
                        val id = getAttributeValue(XML_TIPS_TIP_ID) ?: return@parseChildren
                        val src = getAttributeValue(XML_TIPS_TIP_SRC) ?: return@parseChildren
                        add(XmlFile(id, src))
                    }
                }
            }
        }
    }

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("dismissListeners")
    val jsDismissListeners get() = dismissListeners.toTypedArray()
    // endregion Kotlin/JS interop

    enum class Type {
        ARTICLE,
        CYOA,
        LESSON,
        TRACT,
        UNKNOWN;

        internal companion object {
            internal val DEFAULT = TRACT

            internal fun parseOrNull(value: String?) = when (value) {
                XML_TYPE_ARTICLE -> ARTICLE
                XML_TYPE_CYOA -> CYOA
                XML_TYPE_LESSON -> LESSON
                XML_TYPE_TRACT -> TRACT
                null -> null
                else -> UNKNOWN
            }
        }
    }

    data class XmlFile(internal val name: String?, internal val src: String)
}

@get:AndroidColorInt
val Manifest?.navBarColor get() = this?.navBarColor ?: primaryColor
@get:AndroidColorInt
val Manifest?.navBarControlColor get() = this?.navBarControlColor ?: primaryTextColor

@get:AndroidColorInt
val Manifest?.lessonNavBarColor get() = this?.navBarColor ?: DEFAULT_LESSON_NAV_BAR_COLOR
@get:AndroidColorInt
val Manifest?.lessonNavBarControlColor get() = this?.navBarControlColor ?: primaryColor

@get:AndroidColorInt
val Manifest?.backgroundColor get() = this?.backgroundColor ?: Manifest.DEFAULT_BACKGROUND_COLOR
val Manifest?.backgroundImageGravity get() = this?.backgroundImageGravity ?: Manifest.DEFAULT_BACKGROUND_IMAGE_GRAVITY
val Manifest?.backgroundImageScaleType
    get() = this?.backgroundImageScaleType ?: Manifest.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

@get:AndroidColorInt
val Manifest?.categoryLabelColor get() = this?.categoryLabelColor ?: textColor
