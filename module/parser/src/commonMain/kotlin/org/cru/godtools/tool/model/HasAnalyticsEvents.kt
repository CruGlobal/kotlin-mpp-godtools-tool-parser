package org.cru.godtools.tool.model

interface HasAnalyticsEvents {
    fun getAnalyticsEvents(type: AnalyticsEvent.Trigger): List<AnalyticsEvent>
}
