package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.TEST_XML_PULL_PARSER_FACTORY
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunOnAndroidWith(AndroidJUnit4::class)
class ManifestTest : UsesResources {
    override val resourcesDir = "model"

    // region parse Manifest
    @Test
    fun testParseManifestEmpty() {
        val manifest = parseManifest("manifest_empty.xml")
//        assertNull(manifest.title)
//        assertEquals(DEFAULT_LESSON_CONTROL_COLOR, manifest.lessonControlColor)
//        assertEquals(DEFAULT_TEXT_SCALE, manifest.textScale, 0.0001)
//        assertEquals(0, manifest.aemImports.size)
//        assertThat(manifest.lessonPages, `is`(empty()))
//        assertThat(manifest.tractPages, `is`(empty()))
//        assertEquals(0, manifest.resources.size)
//        assertEquals(0, manifest.tips.size)
    }

    private fun parseManifest(name: String): Manifest {
        val parser = TEST_XML_PULL_PARSER_FACTORY.getXmlParser(name)!!.apply { nextTag() }
        return Manifest(parser)
    }
    // endregion parse Manifest

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
