package org.cru.godtools.tool.model

import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.DeprecationException
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.fluidlocale.PlatformLocale
import org.cru.godtools.tool.internal.fluidlocale.toLocaleOrNull
import org.cru.godtools.tool.model.ImageGravity.Companion.toImageGravityOrNull
import org.cru.godtools.tool.model.ImageScaleType.Companion.toImageScaleTypeOrNull
import org.cru.godtools.tool.model.Multiselect.Companion.XML_MULTISELECT_OPTION_BACKGROUND_COLOR
import org.cru.godtools.tool.model.Multiselect.Companion.XML_MULTISELECT_OPTION_SELECTED_COLOR
import org.cru.godtools.tool.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.tool.model.lesson.DEFAULT_LESSON_NAV_BAR_COLOR
import org.cru.godtools.tool.model.lesson.LessonPage
import org.cru.godtools.tool.model.lesson.XMLNS_LESSON
import org.cru.godtools.tool.model.page.DEFAULT_CONTROL_COLOR
import org.cru.godtools.tool.model.page.Page
import org.cru.godtools.tool.model.page.XMLNS_PAGE
import org.cru.godtools.tool.model.page.XML_CONTROL_COLOR
import org.cru.godtools.tool.model.tips.Tip
import org.cru.godtools.tool.model.tract.TractPage
import org.cru.godtools.tool.model.tract.XMLNS_TRACT
import org.cru.godtools.tool.model.tract.XML_CARD_BACKGROUND_COLOR
import org.cru.godtools.tool.util.setOnce
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_MANIFEST = "manifest"
private const val XML_TOOL = "tool"
private const val XML_LOCALE = "locale"
private const val XML_TYPE = "type"
private const val XML_TYPE_ARTICLE = "article"
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

@OptIn(ExperimentalStdlibApi::class)
class Manifest : BaseModel, Styles {
    internal companion object {
        @AndroidColorInt
        internal val DEFAULT_PRIMARY_COLOR = color(59, 164, 219, 1.0)
        @AndroidColorInt
        internal val DEFAULT_PRIMARY_TEXT_COLOR = color(255, 255, 255, 1.0)

        @AndroidColorInt
        internal val DEFAULT_BACKGROUND_COLOR = color(255, 255, 255, 1.0)
        internal val DEFAULT_BACKGROUND_IMAGE_GRAVITY = ImageGravity.CENTER
        internal val DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE = ImageScaleType.FILL

        internal val DEFAULT_BUTTON_STYLE = Button.Style.CONTAINED

        @AndroidColorInt
        internal val DEFAULT_TEXT_COLOR = color(90, 90, 90, 1.0)
        internal val DEFAULT_TEXT_ALIGN = Text.Align.START

        internal suspend fun parse(fileName: String, parseFile: suspend (String) -> XmlPullParser): Manifest {
            val manifest = Manifest(parseFile(fileName))
            coroutineScope {
                // parse pages
                launch {
                    manifest.pages = manifest.pagesToParse
                        .map { (fileName, src) -> async { Page.parse(manifest, fileName, parseFile(src)) } }
                        .awaitAll().filterNotNull()
                }

                // parse tips
                launch {
                    manifest.tips = manifest.tipsToParse
                        .map { (id, src) -> async { Tip(manifest, id, parseFile(src)) } }
                        .awaitAll()
                        .associateBy { it.id }
                }
            }
            return manifest
        }
    }

    val code: String?
    val locale: PlatformLocale?
    val type: Type

    val dismissListeners: Set<EventId>

    @AndroidColorInt
    override val primaryColor: PlatformColor
    @AndroidColorInt
    override val primaryTextColor: PlatformColor

    @AndroidColorInt
    private val _navBarColor: PlatformColor?
    @get:AndroidColorInt
    internal val navBarColor
        get() = _navBarColor ?: if (type == Type.LESSON) DEFAULT_LESSON_NAV_BAR_COLOR else primaryColor
    @AndroidColorInt
    private val _navBarControlColor: PlatformColor?
    @get:AndroidColorInt
    internal val navBarControlColor
        get() = _navBarControlColor ?: if (type == Type.LESSON) primaryColor else primaryTextColor

    @AndroidColorInt
    internal val backgroundColor: PlatformColor
    private val _backgroundImage: String?
    val backgroundImage get() = getResource(_backgroundImage)
    internal val backgroundImageGravity: ImageGravity
    internal val backgroundImageScaleType: ImageScaleType

    @AndroidColorInt
    private val _cardBackgroundColor: PlatformColor?
    @get:AndroidColorInt
    val cardBackgroundColor get() = _cardBackgroundColor ?: backgroundColor

    @AndroidColorInt
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

    val categories: List<Category>
    var pages: List<Page> by setOnce()
        private set
    @Deprecated("Since v0.4.0, use pages instead which will support different page types in the future.")
    val lessonPages get() = pages.filterIsInstance<LessonPage>()
    @Deprecated("Since v0.4.0, use pages instead which will support different page types in the future.")
    val tractPages get() = pages.filterIsInstance<TractPage>()
    val aemImports: List<Uri>

    // XXX: make this visible to aid in iOS migration
    val resources: Map<String?, Resource>
    var tips: Map<String, Tip> by setOnce()
        private set

    private val pagesToParse: List<Pair<String?, String>>
    private val tipsToParse: List<Pair<String, String>>

    private constructor(parser: XmlPullParser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_MANIFEST, XML_MANIFEST)

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
        backgroundImageGravity = parser.getAttributeValue(XML_BACKGROUND_IMAGE_GRAVITY)?.toImageGravityOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY
        backgroundImageScaleType = parser.getAttributeValue(XML_BACKGROUND_IMAGE_SCALE_TYPE)?.toImageScaleTypeOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

        _cardBackgroundColor = parser.getAttributeValue(XMLNS_TRACT, XML_CARD_BACKGROUND_COLOR)?.toColorOrNull()
        _categoryLabelColor = parser.getAttributeValue(XML_CATEGORY_LABEL_COLOR)?.toColorOrNull()
        val lessonControlColor = parser.getAttributeValue(XMLNS_LESSON, XML_CONTROL_COLOR)?.toColorOrNull()?.also {
            val message = "Deprecated lesson:control-color defined on tool: $code language: $locale"
            Napier.e(message, DeprecationException(message), "Manifest")
        }
        pageControlColor =
            parser.getAttributeValue(XMLNS_PAGE, XML_CONTROL_COLOR)?.toColorOrNull() ?: lessonControlColor
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
        val resources = mutableListOf<Resource>()
        pagesToParse = mutableListOf()
        tipsToParse = mutableListOf()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_MANIFEST -> when (parser.name) {
                    XML_TITLE -> title = parser.parseTextChild(this, XMLNS_MANIFEST, XML_TITLE)
                    XML_CATEGORIES -> categories += parser.parseCategories()
                    XML_PAGES -> {
                        val result = parser.parsePages()
                        aemImports += result.aemImports
                        pagesToParse += result.pages
                    }
                    XML_RESOURCES -> resources += parser.parseResources()
                    XML_TIPS -> tipsToParse += parser.parseTips()
                }
            }
        }

        _title = title
        this.resources = resources.associateBy { it.name }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    constructor(
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
        multiselectOptionSelectedColor: PlatformColor? = null,
        textColor: PlatformColor = DEFAULT_TEXT_COLOR,
        textScale: Double = DEFAULT_TEXT_SCALE,
        resources: ((Manifest) -> List<Resource>)? = null,
        tips: ((Manifest) -> List<Tip>)? = null,
        pages: ((Manifest) -> List<Page>)? = null
    ) {
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

        _multiselectOptionBackgroundColor = null
        this.multiselectOptionSelectedColor = multiselectOptionSelectedColor

        this.textColor = textColor
        this.textScale = textScale

        _title = null

        aemImports = emptyList()
        categories = emptyList()
        this.pages = pages?.invoke(this).orEmpty()
        this.resources = resources?.invoke(this)?.associateBy { it.name }.orEmpty()
        this.tips = tips?.invoke(this)?.associateBy { it.id }.orEmpty()

        pagesToParse = emptyList()
        tipsToParse = emptyList()
    }

    override val manifest get() = this
    internal fun getResource(name: String?) = name?.let { resources[name] }

    fun findCategory(category: String?) = categories.firstOrNull { it.id == category }
    fun findTractPage(id: String?) = tractPages.firstOrNull { it.id.equals(id, ignoreCase = true) }
    fun findTip(id: String?) = tips[id]

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
        val pages = mutableListOf<Pair<String?, String>>()
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
                        result.pages += fileName to src
                    }
                }
                XMLNS_ARTICLE -> when (name) {
                    XML_PAGES_AEM_IMPORT -> getAttributeValue(XML_PAGES_AEM_IMPORT_SRC)?.toUri()
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
                        add(id to src)
                    }
                }
            }
        }
    }

    enum class Type {
        TRACT, ARTICLE, LESSON, UNKNOWN;

        internal companion object {
            internal val DEFAULT = TRACT

            internal fun parseOrNull(value: String?) = when (value) {
                XML_TYPE_ARTICLE -> ARTICLE
                XML_TYPE_LESSON -> LESSON
                XML_TYPE_TRACT -> TRACT
                null -> null
                else -> UNKNOWN
            }
        }
    }
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

internal fun Base.getResource(name: String?) = manifest.getResource(name)
