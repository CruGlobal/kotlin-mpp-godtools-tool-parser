package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.model.Input

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RenderInputTest : BaseRenderContentTest() {
    companion object {
        const val NAME = "name"
    }

    override val testModel = Input(
        name = NAME,
        invisibleIf = invisibleIf,
        goneIf = goneIf,
    )

    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithTag(TEST_TAG_INPUT)

    @Test
    fun `Action - Text Input`() = runComposeUiTest {
        setContent {
            RenderContentStack(
                listOf(testModel),
                state = state
            )
        }

        onModelNode().performTextInput("test")
        assertEquals("test", state.formFieldValue(NAME))
    }

    @Test
    fun `UI - Show Text From State`() = runComposeUiTest {
        state.updateFormFieldValue(NAME, "value")

        setContent {
            RenderContentStack(
                listOf(testModel),
                state = state
            )
        }

        onModelNode().assertTextContains("value")
    }
}
