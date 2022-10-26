package org.cru.godtools.shared.tool.parser.model

interface HasAnalyticsEvents {
    fun getAnalyticsEvents(type: AnalyticsEvent.Trigger): List<AnalyticsEvent>
}
