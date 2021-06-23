package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@RunOnAndroidWith(AndroidJUnit4::class)
class CategoryTest : UsesResources() {
    @Test
    fun testParseCategory() {
        val category = Manifest.parse("categories.xml") { getTestXmlParser(it) }.categories.single()
        assertEquals("testParseCategory", category.id)
        assertNotNull(category.banner).also {
            assertEquals("banner.jpg", it.name)
            assertEquals("bannersha1.jpg", it.localName)
        }
        assertEquals(setOf("tag1", "tag2"), category.aemTags)
        assertEquals("Category", category.label!!.text)
        assertEquals(TestColors.RED, category.label!!.textColor)
    }

    @Test
    fun testLabelTextColor() {
        val manifest = Manifest(categoryLabelColor = TestColors.BLUE)
        with(Category(manifest, label = { Text(it) })) {
            assertEquals(manifest.categoryLabelColor, label!!.textColor)
            assertNotEquals(manifest.textColor, label!!.textColor)
        }

        with(Category(manifest, label = { Text(it, textColor = TestColors.GREEN) })) {
            assertEquals(TestColors.GREEN, label!!.textColor)
            assertNotEquals(manifest.categoryLabelColor, label!!.textColor)
            assertNotEquals(manifest.textColor, label!!.textColor)
        }
    }

    @Test
    fun testLabelTextSize() {
        with(Category(label = { Text(it) })) {
            assertEquals(TEXT_SIZE_CATEGORY, (TEXT_SIZE_BASE * label!!.textScale).toInt())
        }

        with(Category(Manifest(textScale = 2.0), label = { Text(it) })) {
            assertEquals(2 * TEXT_SIZE_CATEGORY, (TEXT_SIZE_BASE * label!!.textScale).toInt())
        }

        with(Category(label = { Text(it, textScale = 2.0) })) {
            assertEquals(2 * TEXT_SIZE_CATEGORY, (TEXT_SIZE_BASE * label!!.textScale).toInt())
        }
    }
}
