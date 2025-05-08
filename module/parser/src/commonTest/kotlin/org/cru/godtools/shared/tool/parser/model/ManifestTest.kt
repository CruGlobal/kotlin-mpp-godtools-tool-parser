package org.cru.godtools.shared.tool.parser.model

import io.fluidsonic.locale.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.ccci.gto.support.fluidsonic.locale.toCommon
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_PAGE_COLLECTION
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.internal.color
import org.cru.godtools.shared.tool.parser.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.shared.tool.parser.model.lesson.DEFAULT_LESSON_NAV_BAR_COLOR
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.cru.godtools.shared.tool.parser.model.page.CardCollectionPage
import org.cru.godtools.shared.tool.parser.model.page.ContentPage
import org.cru.godtools.shared.tool.parser.model.page.DEFAULT_CONTROL_COLOR
import org.cru.godtools.shared.tool.parser.model.page.PageCollectionPage
import org.cru.godtools.shared.tool.parser.model.shareable.ShareableImage
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

@RunOnAndroidWith(AndroidJUnit4::class)
class ManifestTest : UsesResources() {
    // region parse Manifest
    @Test
    fun testParseManifestEmpty() = runTest {
        val manifest = parseManifest("manifest_empty.xml")
        assertNull(manifest.title)
        assertNull(manifest.code)
        assertNull(manifest.locale)
        assertEquals(Manifest.Type.DEFAULT, manifest.type)

        assertEquals(Manifest.DEFAULT_PRIMARY_COLOR, manifest.primaryColor)
        assertEquals(Manifest.DEFAULT_PRIMARY_TEXT_COLOR, manifest.primaryTextColor)

        assertEquals(Manifest.DEFAULT_BACKGROUND_COLOR, manifest.backgroundColor)
        assertEquals(Manifest.DEFAULT_BACKGROUND_IMAGE_GRAVITY, manifest.backgroundImageGravity)
        assertEquals(Manifest.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE, manifest.backgroundImageScaleType)

        assertEquals(manifest.backgroundColor.toRGB(), manifest.cardBackgroundColor)
        assertEquals(manifest.textColor, manifest.categoryLabelColor.toRGB())
        assertEquals(DEFAULT_CONTROL_COLOR, manifest.pageControlColor)

        assertEquals(manifest.backgroundColor, manifest.multiselectOptionBackgroundColor)
        assertNull(manifest.multiselectOptionSelectedColor)

        assertEquals(Manifest.DEFAULT_TEXT_COLOR, manifest.textColor)
        assertEquals(DEFAULT_TEXT_SCALE, manifest.textScale, 0.0001)
        assertEquals(0, manifest.aemImports.size)
        assertTrue(manifest.pages.isEmpty())
        assertTrue(manifest.resources.isEmpty())
        assertTrue(manifest.shareables.isEmpty())
        assertFalse(manifest.hasTips)
    }

    @Test
    fun testParseManifestArticle() = runTest {
        val manifest = parseManifest("manifest_article.xml")
        assertEquals(Manifest.Type.ARTICLE, manifest.type)
        assertEquals(TestColors.GREEN.toPlatformColor(), manifest.categoryLabelColor)
        assertEquals(TestColors.RED, manifest.textColor)
        val category = assertNotNull(manifest.findCategory("testParseCategory"))
        assertEquals("testParseCategory", category.id)
        val banner = assertNotNull(category.banner)
        assertEquals("banner.jpg", banner.name)
        assertEquals("bannersha1.jpg", banner.localName)
        assertEquals(setOf("tag1", "tag2"), category.aemTags)
        val label = assertNotNull(category.label)
        assertEquals("Category", label.text)
        assertEquals(1, manifest.aemImports.size)
        assertEquals("https://www.example.com", manifest.aemImports.single().toString())
    }

    @Test
    fun testParseManifestCyoa() = runTest {
        val manifest = parseManifest("manifest_cyoa.xml")
        assertEquals("title", manifest.title)
        assertEquals("cyoa1", manifest.code)
        assertEquals(Locale.forLanguage("fr"), manifest.locale?.toCommon())
        assertEquals(Manifest.Type.CYOA, manifest.type)

        assertEquals(1, manifest.pages.size)
        assertEquals("page0.xml", manifest.pages[0].fileName)
    }

    @Test
    fun testParseManifestLesson() = runTest {
        val manifest = parseManifest("manifest_lesson.xml")
        assertEquals("title", manifest.title)
        assertEquals("lesson1", manifest.code)
        assertEquals(Locale.forLanguage("ar"), manifest.locale?.toCommon())
        assertEquals(Manifest.Type.LESSON, manifest.type)
        assertEquals(TestColors.RED.toPlatformColor(), manifest.pageControlColor)
        assertEquals(EventId.parse("dismiss_event").toSet(), manifest.dismissListeners)

        assertEquals(TestColors.RED.toPlatformColor(), manifest.multiselectOptionBackgroundColor)
        assertEquals(TestColors.GREEN.toPlatformColor(), manifest.multiselectOptionSelectedColor)

        assertEquals(1, manifest.pages.size)
        assertEquals("page0.xml", manifest.pages[0].fileName)
        assertEquals(1, manifest.pages.size)
        assertEquals("page0.xml", manifest.pages[0].fileName)
        assertEquals("page_defaults", manifest.pages[0].id)
    }

    @Test
    fun testParseManifestTract() = runTest {
        val manifest = parseManifest("manifest_tract.xml")
        assertEquals("title", manifest.title)
        assertEquals(Manifest.Type.TRACT, manifest.type)
        assertEquals(TestColors.RED, manifest.primaryColor)
        assertEquals(TestColors.BLUE, manifest.primaryTextColor)
        assertEquals(TestColors.GREEN.toPlatformColor(), manifest.navBarColor)
        assertEquals(color(255, 0, 255, 1.0).toPlatformColor(), manifest.navBarControlColor)
        assertEquals(color(255, 255, 0, 1.0).toPlatformColor(), manifest.pageControlColor)
        assertEquals(1.2345, manifest.textScale, 0.00001)
        assertEquals(2, manifest.pages.size)
        assertEquals("page0.xml", manifest.pages[0].fileName)
        assertEquals(0, manifest.pages[0].position)
        assertEquals(null, manifest.pages[1].fileName)
        assertEquals(1, manifest.pages[1].position)
    }

    @Test
    fun testParseManifestBackgroundAttrs() = runTest {
        val manifest = parseManifest("manifest_background.xml")
        assertEquals(TestColors.GREEN.toPlatformColor(), manifest.backgroundColor)
        assertEquals(TestColors.BLUE, manifest.cardBackgroundColor)
        assertEquals(ImageScaleType.FIT, manifest.backgroundImageScaleType)
        assertTrue(manifest.backgroundImageGravity.isTop)
        assertTrue(manifest.backgroundImageGravity.isEnd)
        val backgroundImage = assertNotNull(manifest.backgroundImage)
        assertEquals("file.jpg", backgroundImage.name)
        assertEquals("sha1.jpg", backgroundImage.localName)
    }

    @Test
    fun testParseManifestContainingTips() = runTest {
        val manifest = parseManifest("manifest_tips.xml")
        assertEquals(0, manifest.pages.size)
        assertEquals(0, manifest.resources.size)
        assertTrue(manifest.hasTips)
        assertEquals("tip1", manifest.findTip("tip1")!!.id)
    }

    @Test
    fun testParseManifestInvalidTips() = runTest {
        val manifest = parseManifest("manifest_tips_invalid.xml")
        assertEquals(0, manifest.pages.size)
        assertEquals(0, manifest.resources.size)
        assertFalse(manifest.hasTips)
    }

    @Test
    fun testParseManifestShareables() = runTest {
        val manifest = parseManifest("manifest_shareables.xml")
        assertEquals(3, manifest.shareables.size)
        with(manifest.findShareable("shareable0")!!) {
            assertSame(manifest.shareables[0], this)
            assertEquals("shareable0", id)
        }
        with(manifest.findShareable("shareable1")!!) {
            assertSame(manifest.shareables[1], this)
            assertEquals("shareable1", id)
        }
        with(manifest.findShareable("shareable_last")!!) {
            assertSame(manifest.shareables[2], this)
            assertEquals("shareable_last", id)
        }
    }

    @Test
    fun testParseManifestWithoutParsingRelated() = runTest {
        val expectedRelatedFiles = setOf(
            "page1_sha.xml",
            "page2_sha.xml",
            "tip1_sha.xml",
            "tip2_sha.xml",
            "file1_sha.png",
            "file2_sha.png",
            "common_sha.xml"
        )

        val manifest = parseManifest("manifest_related_files.xml", ParserConfig().withParseRelated(false))
        assertTrue(manifest.pages.isEmpty())
        assertTrue(manifest.tips.isEmpty())
        assertTrue(manifest.hasTips)
        assertEquals(expectedRelatedFiles, manifest.relatedFiles)
    }

    @Test
    fun testParseManifestEmptyWithoutParsingRelated() = runTest {
        val manifest = parseManifest("manifest_empty.xml", ParserConfig().withParseRelated(false))
        assertTrue(manifest.relatedFiles.isEmpty())
        assertTrue(manifest.pages.isEmpty())
        assertFalse(manifest.hasTips)
    }

    private suspend fun parseManifest(name: String, config: ParserConfig = ParserConfig()) =
        Manifest.parse(name, config) { getTestXmlParser(it) }
    // endregion parse Manifest

    // region HasPages
    @Test
    fun testManifestFindPage() {
        val manifest = Manifest(code = "tool", pages = { manifest -> List(10) { TractPage(manifest) } })
        assertNull(manifest.findPage("invalid"))
        manifest.pages.forEach { page ->
            assertSame(page, manifest.findPage(page.id))
        }
    }

    // region HasPages.supportsPageType()
    @Test
    fun testManifestSupportsPageType_Article() {
        val manifest = Manifest(type = Manifest.Type.ARTICLE)
        assertFalse(manifest.supportsPageType(ContentPage::class))
        assertFalse(manifest.supportsPageType(LessonPage::class))
        assertFalse(manifest.supportsPageType(TractPage::class))
    }

    @Test
    fun testManifestSupportsPageType_Cyoa() {
        val manifest = Manifest(
            config = ParserConfig().withSupportedFeatures(FEATURE_PAGE_COLLECTION),
            type = Manifest.Type.CYOA
        )
        assertTrue(manifest.supportsPageType(ContentPage::class))
        assertTrue(manifest.supportsPageType(CardCollectionPage::class))
        assertTrue(manifest.supportsPageType(PageCollectionPage::class))
        assertFalse(manifest.supportsPageType(LessonPage::class))
        assertFalse(manifest.supportsPageType(TractPage::class))

        assertFalse(
            Manifest(type = Manifest.Type.CYOA).supportsPageType(PageCollectionPage::class),
            "PageCollectionPages are only supported when the feature is enabled",
        )
    }

    @Test
    fun testManifestSupportsPageType_Lesson() {
        val manifest = Manifest(type = Manifest.Type.LESSON)
        assertTrue(manifest.supportsPageType(LessonPage::class))
        assertFalse(manifest.supportsPageType(ContentPage::class))
        assertFalse(manifest.supportsPageType(TractPage::class))
    }

    @Test
    fun testManifestSupportsPageType_Tract() {
        val manifest = Manifest(type = Manifest.Type.TRACT)
        assertTrue(manifest.supportsPageType(TractPage::class))
        assertFalse(manifest.supportsPageType(ContentPage::class))
        assertFalse(manifest.supportsPageType(LessonPage::class))
    }
    // endregion Manifest.supportsPageType()
    // endregion HasPages

    @Test
    fun testManifestFindShareable() {
        val manifest = Manifest(code = "tool", shareables = { List(5) { ShareableImage(id = "shareable$it") } })
        assertNull(manifest.findShareable(null))
        assertNull(manifest.findShareable("invalid"))
        manifest.shareables.forEach { page ->
            assertSame(page, manifest.findShareable(page.id))
        }
    }

    // region Fallback Behaviors
    @Test
    fun testCardBackgroundColorFallbackBehavior() {
        assertEquals(TestColors.GREEN, Manifest(cardBackgroundColor = TestColors.GREEN).cardBackgroundColor)
        assertEquals(TestColors.BLUE, Manifest(backgroundColor = TestColors.BLUE.toPlatformColor()).cardBackgroundColor)
    }

    @Test
    fun testCategoryLabelColorFallbackBehavior() {
        with(null as Manifest?) {
            assertEquals(Manifest.DEFAULT_TEXT_COLOR.toPlatformColor(), categoryLabelColor)
        }
        with(Manifest()) {
            assertEquals(Manifest.DEFAULT_TEXT_COLOR.toPlatformColor(), categoryLabelColor)
            assertEquals(Manifest.DEFAULT_TEXT_COLOR.toPlatformColor(), (this as Manifest?).categoryLabelColor)
        }
        with(Manifest(textColor = TestColors.GREEN)) {
            assertEquals(TestColors.GREEN.toPlatformColor(), categoryLabelColor)
            assertEquals(TestColors.GREEN.toPlatformColor(), (this as Manifest?).categoryLabelColor)
        }
        with(Manifest(textColor = TestColors.RED, categoryLabelColor = TestColors.GREEN.toPlatformColor())) {
            assertEquals(TestColors.GREEN.toPlatformColor(), categoryLabelColor)
            assertEquals(TestColors.GREEN.toPlatformColor(), (this as Manifest?).categoryLabelColor)
        }
    }
    // endregion Fallback Behaviors

    // region navbar colors
    @Test
    fun testNavBarColors() {
        val manifestNull: Manifest? = null
        assertEquals(Manifest.DEFAULT_PRIMARY_COLOR.toPlatformColor(), manifestNull.navBarColor)
        assertEquals(Manifest.DEFAULT_PRIMARY_TEXT_COLOR.toPlatformColor(), manifestNull.navBarControlColor)

        val manifestPrimary = Manifest(primaryColor = TestColors.random(), primaryTextColor = TestColors.random())
        assertEquals(manifestPrimary.primaryColor.toPlatformColor(), manifestPrimary.navBarColor)
        assertEquals(manifestPrimary.primaryColor.toPlatformColor(), (manifestPrimary as Manifest?).navBarColor)
        assertEquals(manifestPrimary.primaryTextColor.toPlatformColor(), manifestPrimary.navBarControlColor)
        assertEquals(
            manifestPrimary.primaryTextColor.toPlatformColor(),
            (manifestPrimary as Manifest?).navBarControlColor
        )

        val manifestNavBar = Manifest(
            primaryColor = TestColors.random(),
            primaryTextColor = TestColors.random(),
            navBarColor = TestColors.GREEN.toPlatformColor(),
            navBarControlColor = TestColors.BLUE.toPlatformColor()
        )
        assertNotEquals(manifestNavBar.primaryColor.toPlatformColor(), manifestNavBar.navBarColor)
        assertEquals(TestColors.GREEN.toPlatformColor(), manifestNavBar.navBarColor)
        assertEquals(TestColors.GREEN.toPlatformColor(), (manifestNavBar as Manifest?).navBarColor)
        assertNotEquals(manifestNavBar.primaryTextColor.toPlatformColor(), manifestNavBar.navBarControlColor)
        assertEquals(TestColors.BLUE.toPlatformColor(), manifestNavBar.navBarControlColor)
        assertEquals(TestColors.BLUE.toPlatformColor(), (manifestNavBar as Manifest?).navBarControlColor)
    }

    @Test
    fun testLessonNavBarColors() {
        val manifestNull: Manifest? = null
        assertEquals(DEFAULT_LESSON_NAV_BAR_COLOR, manifestNull.lessonNavBarColor)
        assertEquals(Manifest.DEFAULT_PRIMARY_COLOR.toPlatformColor(), manifestNull.lessonNavBarControlColor)

        val manifestPrimary = Manifest(
            type = Manifest.Type.LESSON,
            primaryColor = TestColors.GREEN,
            primaryTextColor = TestColors.RED
        )
        assertEquals(DEFAULT_LESSON_NAV_BAR_COLOR, manifestPrimary.navBarColor)
        assertEquals(DEFAULT_LESSON_NAV_BAR_COLOR, manifestPrimary.lessonNavBarColor)
        assertEquals(TestColors.GREEN.toPlatformColor(), manifestPrimary.navBarControlColor)
        assertEquals(TestColors.GREEN.toPlatformColor(), manifestPrimary.lessonNavBarControlColor)

        val manifestNavBar = Manifest(
            type = Manifest.Type.LESSON,
            primaryColor = TestColors.RED,
            primaryTextColor = TestColors.RED,
            navBarColor = TestColors.GREEN.toPlatformColor(),
            navBarControlColor = TestColors.BLUE.toPlatformColor()
        )
        assertEquals(TestColors.GREEN.toPlatformColor(), manifestNavBar.navBarColor)
        assertEquals(TestColors.GREEN.toPlatformColor(), manifestNavBar.lessonNavBarColor)
        assertEquals(TestColors.BLUE.toPlatformColor(), manifestNavBar.navBarControlColor)
        assertEquals(TestColors.BLUE.toPlatformColor(), manifestNavBar.lessonNavBarControlColor)
    }
    // endregion navbar colors

    // region Manifest.Type
    @Test
    fun testManifestTypeParsing() {
        assertNull(Manifest.Type.parseOrNull(null))
        assertEquals(Manifest.Type.ARTICLE, Manifest.Type.parseOrNull("article"))
        assertEquals(Manifest.Type.LESSON, Manifest.Type.parseOrNull("lesson"))
        assertEquals(Manifest.Type.TRACT, Manifest.Type.parseOrNull("tract"))
        assertEquals(Manifest.Type.UNKNOWN, Manifest.Type.parseOrNull("nasldkja"))
    }
    // endregion Manifest.Type
}
