package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunOnAndroidWith(AndroidJUnit4::class)
class CategoryTest : UsesResources() {
    @Test
    fun testParseCategory() {
        val category = Manifest.parse("categories.xml") { getTestXmlParser(it) }.categories.single()
        assertEquals("testParseCategory", category.id)
        assertNotNull(category.banner).also { banner ->
            assertEquals("banner.jpg", banner.name)
            assertEquals("bannersha1.jpg", banner.localName)
        }
        assertEquals(setOf("tag1", "tag2"), category.aemTags)
        assertEquals("Category", category.label?.text)
    }
}
