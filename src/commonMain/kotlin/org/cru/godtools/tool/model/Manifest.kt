package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.internal.fluidlocale.PlatformLocale
import org.cru.godtools.tool.internal.fluidlocale.toLocaleOrNull
import org.cru.godtools.tool.model.ImageGravity.Companion.toImageGravityOrNull
import org.cru.godtools.tool.model.ImageScaleType.Companion.toImageScaleTypeOrNull
import org.cru.godtools.tool.model.lesson.DEFAULT_LESSON_CONTROL_COLOR
import org.cru.godtools.tool.model.lesson.DEFAULT_LESSON_NAV_BAR_COLOR
import org.cru.godtools.tool.model.lesson.LessonPage
import org.cru.godtools.tool.model.lesson.XMLNS_LESSON
import org.cru.godtools.tool.model.lesson.XML_CONTROL_COLOR
import org.cru.godtools.tool.model.tips.Tip
import org.cru.godtools.tool.model.tract.XMLNS_TRACT
import org.cru.godtools.tool.model.tract.XML_CARD_BACKGROUND_COLOR
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
        internal const val DEFAULT_TEXT_SCALE = 1.0
        internal val DEFAULT_TEXT_ALIGN = Text.Align.START

        internal fun parse(fileName: String, parseFile: (String) -> XmlPullParser) =
            Manifest(parseFile(fileName), parseFile)
    }

    val code: String?
    val locale: PlatformLocale?
    val type: Type

    val dismissListeners: Set<EventId>

    @AndroidColorInt
    override val primaryColor: Color
    @AndroidColorInt
    override val primaryTextColor: Color

    @AndroidColorInt
    private val _navBarColor: Color?
    @get:AndroidColorInt
    val navBarColor get() = _navBarColor ?: if (type == Type.LESSON) DEFAULT_LESSON_NAV_BAR_COLOR else primaryColor
    @AndroidColorInt
    private val _navBarControlColor: Color?
    @get:AndroidColorInt
    val navBarControlColor get() = _navBarControlColor ?: if (type == Type.LESSON) primaryColor else primaryTextColor

    @AndroidColorInt
    val backgroundColor: Color
    private val _backgroundImage: String?
    val backgroundImage get() = getResource(_backgroundImage)
    val backgroundImageGravity: ImageGravity
    val backgroundImageScaleType: ImageScaleType

    @AndroidColorInt
    private val _cardBackgroundColor: Color?
    @get:AndroidColorInt
    val cardBackgroundColor get() = _cardBackgroundColor ?: backgroundColor

    @AndroidColorInt
    private val _categoryLabelColor: Color?
    @get:AndroidColorInt
    internal val categoryLabelColor get() = _categoryLabelColor ?: textColor

    @AndroidColorInt
    internal val lessonControlColor: Color

    override val buttonStyle get() = DEFAULT_BUTTON_STYLE

    @AndroidColorInt
    override val textColor: Color
    override val textScale: Double

    private val _title: Text?
    val title: String? get() = _title?.text

    val categories: List<Category>
    val lessonPages: List<LessonPage>

    @VisibleForTesting
    internal val resources: Map<String?, Resource>
    val tips: Map<String, Tip>

    private constructor(parser: XmlPullParser, parseFile: (String) -> XmlPullParser) {
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
        lessonControlColor =
            parser.getAttributeValue(XMLNS_LESSON, XML_CONTROL_COLOR)?.toColorOrNull() ?: DEFAULT_LESSON_CONTROL_COLOR

        textColor = parser.getAttributeValue(XML_TEXT_COLOR)?.toColorOrNull() ?: DEFAULT_TEXT_COLOR
        textScale = parser.getAttributeValue(XML_TEXT_SCALE)?.toDoubleOrNull() ?: DEFAULT_TEXT_SCALE

        var title: Text? = null
        val categories = mutableListOf<Category>()
        val lessonPages = mutableListOf<LessonPage>()
        val resources = mutableListOf<Resource>()
        val tips = mutableListOf<Tip>()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_MANIFEST -> when (parser.name) {
                    XML_TITLE -> title = parser.parseTextChild(this, XMLNS_MANIFEST, XML_TITLE)
                    XML_CATEGORIES -> categories += parser.parseCategories()
                    XML_PAGES -> {
                        val result = parser.parsePages(parseFile)
                        lessonPages += result.lessonPages
                    }
                    XML_RESOURCES -> resources += parser.parseResources()
                    XML_TIPS -> tips += parser.parseTips(parseFile)
                }
            }
        }

        _title = title
        this.categories = categories
        this.lessonPages = lessonPages
        this.resources = resources.associateBy { it.name }
        this.tips = tips.associateBy { it.id }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        type: Type = Type.DEFAULT,
        code: String? = null,
        primaryColor: Color = DEFAULT_PRIMARY_COLOR,
        primaryTextColor: Color = DEFAULT_PRIMARY_TEXT_COLOR,
        navBarColor: Color? = null,
        navBarControlColor: Color? = null,
        backgroundColor: Color = DEFAULT_BACKGROUND_COLOR,
        cardBackgroundColor: Color? = null,
        categoryLabelColor: Color? = null,
        lessonControlColor: Color = DEFAULT_LESSON_CONTROL_COLOR,
        textColor: Color = DEFAULT_TEXT_COLOR,
        textScale: Double = DEFAULT_TEXT_SCALE,
        resources: ((Manifest) -> List<Resource>)? = null,
        tips: ((Manifest) -> List<Tip>)? = null
    ) {
        this.code = code
        locale = null
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
        this.lessonControlColor = lessonControlColor

        this.textColor = textColor
        this.textScale = textScale

        _title = null

        categories = emptyList()
        lessonPages = emptyList()
        this.resources = resources?.invoke(this)?.associateBy { it.name }.orEmpty()
        this.tips = tips?.invoke(this)?.associateBy { it.id }.orEmpty()
    }

    override val manifest get() = this
    internal fun getResource(name: String?) = name?.let { resources[name] }

    fun findCategory(category: String?) = categories.firstOrNull { it.id == category }
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
        val lessonPages = mutableListOf<LessonPage>()
    }

    private fun XmlPullParser.parsePages(parseFile: (String) -> XmlPullParser) = PagesData().also { result ->
        require(XmlPullParser.START_TAG, XMLNS_MANIFEST, XML_PAGES)

        // process any child elements
        parseChildren {
            when (namespace) {
                XMLNS_MANIFEST -> when (name) {
                    XML_PAGES_PAGE -> {
                        val fileName = getAttributeValue(XML_PAGES_PAGE_FILENAME)
                        val src = getAttributeValue(XML_PAGES_PAGE_SRC)

                        if (src != null) {
                            @Suppress("NON_EXHAUSTIVE_WHEN")
                            when (type) {
                                Type.LESSON -> result.lessonPages += LessonPage(this@Manifest, fileName, parseFile(src))
                            }
                        }
                    }
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

    private fun XmlPullParser.parseTips(parseFile: (String) -> XmlPullParser) = buildList {
        parseChildren {
            when (namespace) {
                XMLNS_MANIFEST -> when (name) {
                    XML_TIPS_TIP -> {
                        val id = getAttributeValue(null, XML_TIPS_TIP_ID)
                        val src = getAttributeValue(null, XML_TIPS_TIP_SRC)
                        if (id != null && src != null)
                            add(Tip(this@Manifest, id, parseFile(src)))
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
