package org.cru.godtools.shared.user.activity.model

import androidx.annotation.VisibleForTesting
import com.github.ajalt.colormath.model.RGB

@ConsistentCopyVisibility
data class Badge internal constructor(
    val type: BadgeType,
    val variant: Int,
    val progress: Int,
    val progressTarget: Int,
) {
    internal companion object {
        val COLORS_NOT_EARNED = IconColors(base = RGB("#d5d5d5")).alpha(0.38f)
    }

    enum class BadgeType(
        @VisibleForTesting internal vararg val variantProgressTargets: Int,
        internal val colors: IconColors
    ) {
        TOOLS_OPENED(1, 5, 10, colors = IconColors(base = RGB("#62CCF3"))),
        LESSONS_COMPLETED(1, 5, 10, colors = IconColors(base = RGB("#A4D7C8"))),
        ARTICLES_OPENED(1, 5, 10, colors = IconColors(base = RGB("#292C67"))),
        IMAGES_SHARED(1, 5, 10, colors = IconColors(base = RGB("#A43E95"))),
        TIPS_COMPLETED(5, 10, 20, colors = IconColors(base = RGB("#E53660"))),
        ;

        internal fun createBadges(progress: Int) = variantProgressTargets.mapIndexed { i, it ->
            Badge(this, variant = i + 1, progress = progress.coerceAtMost(it), progressTarget = it)
        }
    }

    val isEarned get() = progress >= progressTarget
    val colors get() = if (isEarned) type.colors else COLORS_NOT_EARNED
    @Deprecated("Use progressTarget instead", ReplaceWith("progressTarget"))
    val target get() = progressTarget
}
