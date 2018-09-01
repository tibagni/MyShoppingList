package com.tiagobagni.myshoppinglist.analytics

import com.microsoft.appcenter.analytics.Analytics

object EventLogger {
    private const val ARCHIVE_EVENT = "archive_list"
    private const val MAX_ARCHIVE_EVENT = "max_archive_list"

    private const val MAX_ARCHIVE_VALUE = "max_number_of_archived_lists"

    fun logArchiveList() {
        logEvent(ARCHIVE_EVENT)
    }

    fun logChangedMaxArchivedLists(newMax: String) {
        logEvent(MAX_ARCHIVE_EVENT, mapOf(MAX_ARCHIVE_VALUE to newMax))
    }

    private fun logEvent(event: String, args: Map<String, String>? = null) {
        Analytics.trackEvent(event, args)
    }
}