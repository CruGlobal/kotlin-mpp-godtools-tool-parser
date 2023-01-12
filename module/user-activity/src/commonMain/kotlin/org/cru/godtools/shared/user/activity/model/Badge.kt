package org.cru.godtools.shared.user.activity.model

import com.github.ajalt.colormath.model.RGB
import org.ccci.gto.support.androidx.annotation.VisibleForTesting

data class Badge internal constructor(val type: BadgeType, val progress: Int, val target: Int) {
    internal companion object {
        val COLORS_NOT_EARNED = IconColors(base = RGB("#d5d5d5")).alpha(0.38f)
    }

    enum class BadgeType(
        @VisibleForTesting internal vararg val variants: Int,
        internal val colors: IconColors
    ) {
        TOOLS_OPENED(1, 5, 10, colors = IconColors(base = RGB("#62CCF3"))),
        LESSONS_COMPLETED(1, 5, 10, colors = IconColors(base = RGB("#A4D7C8"))),
        ARTICLES_OPENED(1, 5, 10, colors = IconColors(base = RGB("#292C67"))),
        IMAGES_SHARED(1, 5, 10, colors = IconColors(base = RGB("#A43E95"))),
        TIPS_COMPLETED(5, 10, 20, colors = IconColors(base = RGB("#E53660"))),
        ;

        internal fun createBadges(progress: Int) =
            variants.map { Badge(this, progress = progress.coerceAtMost(it), target = it) }
    }

    val isEarned get() = progress >= target
    val colors get() = if (isEarned) type.colors else COLORS_NOT_EARNED
}
