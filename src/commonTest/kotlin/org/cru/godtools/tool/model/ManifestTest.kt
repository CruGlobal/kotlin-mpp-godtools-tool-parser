package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.TEST_XML_PULL_PARSER_FACTORY
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.Test

@RunOnAndroidWith(AndroidJUnit4::class)
class ManifestTest : UsesResources {
    override val resourcesDir = "model"

    @Test
    fun parseManifestEmpty() {
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
}
