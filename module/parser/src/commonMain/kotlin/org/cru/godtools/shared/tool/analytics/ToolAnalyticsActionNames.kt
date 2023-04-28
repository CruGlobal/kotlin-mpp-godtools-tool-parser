package org.cru.godtools.shared.tool.analytics

object ToolAnalyticsActionNames {
    const val ACTION_OPEN = "tool-opened"
    const val ACTION_OPEN_FIRST = "first-tool-opened"

    const val ACTION_SETTINGS = "tool_settings"
    const val ACTION_SHARE = "share_icon_engaged"
    const val ACTION_TOGGLE_LANGUAGE = "parallel_language_toggled"

    // region Content Events
    const val ACTION_CONTENT_EVENT = "content_event"
    const val PARAM_CONTENT_EVENT_ID = "event_id"
    // endregion Content Events

    // region Lesson Feedback
    const val ACTION_LESSON_FEEDBACK = "lesson_feedback"

    const val PARAM_LESSON_FEEDBACK_HELPFUL = "helpful"
    const val PARAM_LESSON_FEEDBACK_READINESS = "readiness"
    const val PARAM_LESSON_FEEDBACK_PAGE_REACHED = "page_reached"

    const val VALUE_LESSON_FEEDBACK_HELPFUL_YES = "yes"
    const val VALUE_LESSON_FEEDBACK_HELPFUL_NO = "no"
    // endregion Lesson Feedback

    // region Share Shareable
    const val ACTION_SHARE_SHAREABLE = "share_shareable"
    const val PARAM_SHAREABLE_ID = "cru_shareable_id"
    // endregion Share Shareable
}
