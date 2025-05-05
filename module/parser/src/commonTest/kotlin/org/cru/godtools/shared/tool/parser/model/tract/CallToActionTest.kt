package org.cru.godtools.shared.tool.parser.model.tract

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.TestColors
import org.cru.godtools.shared.tool.parser.model.toPlatformColor

@RunOnAndroidWith(AndroidJUnit4::class)
class CallToActionTest : UsesResources("model/tract") {
    @Test
    fun testParseCallToAction() = runTest {
        val callToAction = TractPage(Manifest(), null, getTestXmlParser("call_to_action.xml")).callToAction
        assertEquals(TestColors.RED.toPlatformColor(), callToAction.controlColor)
        assertEquals("Call To Action", callToAction.label!!.text)
        assertEquals("tip1", callToAction.tipId)
    }
}
