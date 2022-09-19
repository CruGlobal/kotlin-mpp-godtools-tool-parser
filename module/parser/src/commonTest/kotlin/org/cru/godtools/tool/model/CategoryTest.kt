package org.cru.godtools.tool.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class CategoryTest : UsesResources() {
    @Test
    fun testParseCategory() = runTest {
        val category = Manifest.parse("categories.xml", ParserConfig()) { getTestXmlParser(it) }.categories.single()
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
}
