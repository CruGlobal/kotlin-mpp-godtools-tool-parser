package org.cru.godtools.shared.renderer.content

import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Spacer
import org.cru.godtools.shared.tool.parser.model.Text

class RenderSpacerPaparazziTest : BasePaparazziTest(renderingMode = RenderingMode.SHRINK) {

    @Test
    fun `RenderSpacer() - Auto Spacer On Top`() = contentSnapshot {

        RenderContentStack(
            listOf(
                Spacer(),
                Text(text = "Text On Top"),
                Text(text = "Text On Bottom")
            )
        )
    }

    @Test
    fun `RenderSpacer() - Auto Spacer In Middle`() = contentSnapshot {

        RenderContentStack(
            listOf(
                Text(text = "Text On Top"),
                Spacer(),
                Text(text = "Text On Bottom")
            )
        )
    }

    @Test
    fun `RenderSpacer() - Auto Spacer On Bottom`() = contentSnapshot {

        RenderContentStack(
            listOf(
                Text(text = "Text On Top"),
                Text(text = "Text On Bottom"),
                Spacer()
            )
        )
    }

    @Test
    fun `RenderSpacer() - Fixed Spacer On Top`() = contentSnapshot {

        RenderContentStack(
            listOf(
                Spacer(mode = Spacer.Mode.FIXED, height = 30),
                Text(text = "Text On Top"),
                Text(text = "Text On Bottom")
            )
        )
    }

    @Test
    fun `RenderSpacer() - Fixed Spacer In Middle`() = contentSnapshot {

        RenderContentStack(
            listOf(
                Text(text = "Text On Top"),
                Spacer(mode = Spacer.Mode.FIXED, height = 30),
                Text(text = "Text On Bottom")
            )
        )
    }

    @Test
    fun `RenderSpacer() - Fixed Spacer On Bottom`() = contentSnapshot {

        RenderContentStack(
            listOf(
                Text(text = "Text On Top"),
                Text(text = "Text On Bottom"),
                Spacer(mode = Spacer.Mode.FIXED, height = 30)
            )
        )
    }
}
