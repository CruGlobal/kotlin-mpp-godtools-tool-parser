package org.cru.godtools.shared.user.activity.model

import com.github.ajalt.colormath.model.RGB
import kotlin.test.Test
import org.cru.godtools.shared.user.activity.internal.test.assertEquals

class UserActivityColorsTest {
    @Test
    fun testColors() {
        assertEquals(
            IconColors(
                light = RGB("#006494"),
                dark = RGB("#8fcdff"),
                containerLight = RGB("#cbe6ff"),
                containerDark = RGB("#004b71"),
            ),
            UserActivity.Colors.toolOpens
        )
        assertEquals(
            IconColors(
                light = RGB("#36675b"),
                dark = RGB("#9ed1c2"),
                containerLight = RGB("#b9edde"),
                containerDark = RGB("#1d4f44"),
            ),
            UserActivity.Colors.lessonCompletions
        )
        assertEquals(
            IconColors(
                light = RGB("#ad3310"),
                dark = RGB("#ffb4a1"),
                containerLight = RGB("#ffdbd1"),
                containerDark = RGB("#881f00"),
            ),
            UserActivity.Colors.screenShares
        )
        assertEquals(
            IconColors(
                light = RGB("#52599b"),
                dark = RGB("#bdc2ff"),
                containerLight = RGB("#dfe0ff"),
                containerDark = RGB("#3a4181"),
            ),
            UserActivity.Colors.linksShared
        )
        assertEquals(
            IconColors(
                light = RGB("#3e6838"),
                dark = RGB("#a4d398"),
                containerLight = RGB("#bfefb3"),
                containerDark = RGB("#275023"),
            ),
            UserActivity.Colors.languagesUsed
        )
        assertEquals(
            IconColors(
                light = RGB("#695f00"),
                dark = RGB("#dac91f"),
                containerLight = RGB("#f8e53f"),
                containerDark = RGB("#4f4800"),
            ),
            UserActivity.Colors.sessions
        )
    }
}
