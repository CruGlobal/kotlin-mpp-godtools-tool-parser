package org.cru.godtools.shared.user.activity.model

import org.ccci.gto.support.androidx.annotation.VisibleForTesting

data class Badge(val type: BadgeType, val progress: Int, val target: Int) {
    enum class BadgeType(@VisibleForTesting internal vararg val variants: Int) {
        TOOLS_OPENED(1, 5, 10),
        LESSONS_COMPLETED(1, 5, 10),
        ARTICLES_OPENED(1, 5, 10),
        IMAGES_SHARED(1, 5, 10),
        TIPS_COMPLETED(5, 10, 20),
        ;

        internal fun createBadges(progress: Int) =
            variants.map { Badge(this, progress = progress.coerceAtMost(it), target = it) }
    }

    val isEarned get() = progress >= target
}
