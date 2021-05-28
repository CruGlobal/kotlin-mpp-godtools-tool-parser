package org.cru.godtools.tool.model

import io.fluidsonic.locale.Locale
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.TEST_XML_PULL_PARSER_FACTORY
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.fluidlocale.toCommon
import org.cru.godtools.tool.model.lesson.DEFAULT_LESSON_CONTROL_COLOR
import org.cru.godtools.tool.model.lesson.DEFAULT_LESSON_NAV_BAR_COLOR
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunOnAndroidWith(AndroidJUnit4::class)
class ManifestTest : UsesResources {
    override val resourcesDir = "model"

    // region parse Manifest
    @Test
    fun testParseManifestEmpty() {
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

        assertEquals(manifest.backgroundColor, manifest.cardBackgroundColor)
        assertEquals(DEFAULT_LESSON_CONTROL_COLOR, manifest.lessonControlColor)
        assertEquals(Manifest.DEFAULT_TEXT_SCALE, manifest.textScale, 0.0001)
//        assertEquals(0, manifest.aemImports.size)
//        assertThat(manifest.lessonPages, `is`(empty()))
//        assertThat(manifest.tractPages, `is`(empty()))
        assertEquals(0, manifest.resources.size)
//        assertEquals(0, manifest.tips.size)
    }

    @Test
    fun testParseManifestArticle() {
        val manifest = parseManifest("manifest_article.xml")
        assertEquals(Manifest.Type.ARTICLE, manifest.type)
        val category = assertNotNull(manifest.findCategory("testParseCategory"))
        assertEquals("testParseCategory", category.id)
        val banner = assertNotNull(category.banner)
        assertEquals("banner.jpg", banner.name)
        assertEquals("bannersha1.jpg", banner.localName)
        assertEquals(setOf("tag1", "tag2"), category.aemTags)
        val label = assertNotNull(category.label)
        assertEquals("Category", label.text)
    }

    @Test
    fun testParseManifestLesson() {
        val manifest = parseManifest("manifest_lesson.xml")
        assertEquals("title", manifest.title)
        assertEquals("lesson1", manifest.code)
        assertEquals(Locale.forLanguage("ar"), manifest.locale?.toCommon())
        assertEquals(Manifest.Type.LESSON, manifest.type)
        assertEquals(TestColors.RED, manifest.lessonControlColor)
        assertEquals(EventId.parse("dismiss_event").toSet(), manifest.dismissListeners)
//        assertThat(manifest.tractPages, `is`(empty()))
//        assertEquals(1, manifest.lessonPages.size)
//        assertEquals("page0.xml", manifest.lessonPages[0].fileName)
    }

    @Test
    fun testParseManifestTract() {
        val manifest = parseManifest("manifest_tract.xml")
        assertEquals("title", manifest.title)
        assertEquals(Manifest.Type.TRACT, manifest.type)
        assertEquals(TestColors.RED, manifest.primaryColor)
        assertEquals(TestColors.BLUE, manifest.primaryTextColor)
        assertEquals(TestColors.GREEN, manifest.navBarColor)
        assertEquals(color(255, 0, 255, 1.0), manifest.navBarControlColor)
        assertEquals(1.2345, manifest.textScale, 0.00001)
//        assertThat(manifest.lessonPages, `is`(empty()))
//        assertEquals(2, manifest.tractPages.size)
//        assertEquals("page0.xml", manifest.tractPages[0].fileName)
//        assertEquals(0, manifest.tractPages[0].position)
//        assertEquals(null, manifest.tractPages[1].fileName)
//        assertEquals(1, manifest.tractPages[1].position)
    }

    @Test
    fun testParseManifestBackgroundAttrs() {
        val manifest = parseManifest("manifest_background.xml")
        assertEquals(TestColors.GREEN, manifest.backgroundColor)
        assertEquals(TestColors.BLUE, manifest.cardBackgroundColor)
        assertEquals(ImageScaleType.FIT, manifest.backgroundImageScaleType)
        assertEquals(ImageGravity.TOP or ImageGravity.END, manifest.backgroundImageGravity)
        val backgroundImage = assertNotNull(manifest.backgroundImage)
        assertEquals("file.jpg", backgroundImage.name)
        assertEquals("sha1.jpg", backgroundImage.localName)
    }

    @Test
    fun testParseManifestContainingTips() {
        val manifest = parseManifest("manifest_tips.xml")
//        assertEquals(0, manifest.tractPages.size)
        assertEquals(0, manifest.resources.size)
//        assertEquals(1, manifest.tips.size)
//        assertEquals("tip1", manifest.findTip("tip1")!!.id)
    }

    @Test
    fun testParseManifestInvalidTips() {
        val manifest = parseManifest("manifest_tips_invalid.xml")
//        assertEquals(0, manifest.tractPages.size)
        assertEquals(0, manifest.resources.size)
//        assertEquals(0, manifest.tips.size)
    }

    private fun parseManifest(name: String): Manifest {
        val parser = TEST_XML_PULL_PARSER_FACTORY.getXmlParser(name)!!.apply { nextTag() }
        return Manifest(parser)
    }
    // endregion parse Manifest

    @Test
    fun testCardBackgroundColorFallbackBehavior() {
        assertEquals(TestColors.GREEN, Manifest(cardBackgroundColor = TestColors.GREEN).cardBackgroundColor)
        assertEquals(TestColors.BLUE, Manifest(backgroundColor = TestColors.BLUE).cardBackgroundColor)
    }

    @Test
    fun testCategoryLabelColorFallbackBehavior() {
        with(null as Manifest?) {
            assertEquals(Manifest.DEFAULT_TEXT_COLOR, categoryLabelColor)
        }
        with(Manifest()) {
            assertEquals(Manifest.DEFAULT_TEXT_COLOR, categoryLabelColor)
            assertEquals(Manifest.DEFAULT_TEXT_COLOR, (this as Manifest?).categoryLabelColor)
        }
        with(Manifest(textColor = TestColors.GREEN)) {
            assertEquals(TestColors.GREEN, categoryLabelColor)
            assertEquals(TestColors.GREEN, (this as Manifest?).categoryLabelColor)
        }
        with(Manifest(textColor = TestColors.RED, categoryLabelColor = TestColors.GREEN)) {
            assertEquals(TestColors.GREEN, categoryLabelColor)
            assertEquals(TestColors.GREEN, (this as Manifest?).categoryLabelColor)
        }
    }

    // region navbar colors
    @Test
    fun testNavBarColors() {
        val manifestNull: Manifest? = null
        assertEquals(Manifest.DEFAULT_PRIMARY_COLOR, manifestNull.navBarColor)
        assertEquals(Manifest.DEFAULT_PRIMARY_TEXT_COLOR, manifestNull.navBarControlColor)

        val manifestPrimary = Manifest(primaryColor = TestColors.GREEN, primaryTextColor = TestColors.BLUE)
        assertEquals(TestColors.GREEN, manifestPrimary.navBarColor)
        assertEquals(TestColors.GREEN, (manifestPrimary as Manifest?).navBarColor)
        assertEquals(TestColors.BLUE, manifestPrimary.navBarControlColor)
        assertEquals(TestColors.BLUE, (manifestPrimary as Manifest?).navBarControlColor)

        val manifestNavBar = Manifest(
            primaryColor = TestColors.RED,
            primaryTextColor = TestColors.RED,
            navBarColor = TestColors.GREEN,
            navBarControlColor = TestColors.BLUE
        )
        assertEquals(TestColors.GREEN, manifestNavBar.navBarColor)
        assertEquals(TestColors.GREEN, (manifestNavBar as Manifest?).navBarColor)
        assertEquals(TestColors.BLUE, manifestNavBar.navBarControlColor)
        assertEquals(TestColors.BLUE, (manifestNavBar as Manifest?).navBarControlColor)
    }

    @Test
    fun testLessonNavBarColors() {
        val manifestNull: Manifest? = null
        assertEquals(DEFAULT_LESSON_NAV_BAR_COLOR, manifestNull.lessonNavBarColor)
        assertEquals(Manifest.DEFAULT_PRIMARY_COLOR, manifestNull.lessonNavBarControlColor)

        val manifestPrimary =
            Manifest(type = Manifest.Type.LESSON, primaryColor = TestColors.GREEN, primaryTextColor = TestColors.RED)
        assertEquals(DEFAULT_LESSON_NAV_BAR_COLOR, manifestPrimary.navBarColor)
        assertEquals(DEFAULT_LESSON_NAV_BAR_COLOR, manifestPrimary.lessonNavBarColor)
        assertEquals(TestColors.GREEN, manifestPrimary.navBarControlColor)
        assertEquals(TestColors.GREEN, manifestPrimary.lessonNavBarControlColor)

        val manifestNavBar = Manifest(
            type = Manifest.Type.LESSON,
            primaryColor = TestColors.RED,
            primaryTextColor = TestColors.RED,
            navBarColor = TestColors.GREEN,
            navBarControlColor = TestColors.BLUE
        )
        assertEquals(TestColors.GREEN, manifestNavBar.navBarColor)
        assertEquals(TestColors.GREEN, manifestNavBar.lessonNavBarColor)
        assertEquals(TestColors.BLUE, manifestNavBar.navBarControlColor)
        assertEquals(TestColors.BLUE, manifestNavBar.lessonNavBarControlColor)
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
