package org.cru.godtools.tool.model.tract

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.TestColors
import kotlin.test.Test
import kotlin.test.assertEquals

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class CallToActionTest : UsesResources("model/tract") {
    @Test
    fun testParseCallToAction() = runTest {
        val callToAction = TractPage(Manifest(), null, getTestXmlParser("call_to_action.xml")).callToAction
        assertEquals(TestColors.RED, callToAction.controlColor)
        assertEquals("Call To Action", callToAction.label!!.text)
        assertEquals("tip1", callToAction.tipId)
    }
}
