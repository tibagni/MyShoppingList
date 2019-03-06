package com.tiagobagni.myshoppinglist.settings

import android.os.Bundle
import android.os.Handler
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.tiagobagni.myshoppinglist.FabProvider
import com.tiagobagni.myshoppinglist.R
import com.tiagobagni.myshoppinglist.analytics.EventLogger
import org.koin.android.architecture.ext.viewModel

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    private val fabProvider by lazy { activity as FabProvider }
    private val archivedListsHistorySizePref by lazy {
        initPref(R.string.sp_max_archived_lists) as ListPreference
    }
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fabProvider.hideFab()
        activity?.title = getString(R.string.settings)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)

        val initialValue = archivedListsHistorySizePref.value
        archivedListsHistorySizePref.summary =
                getString(R.string.max_archived_lists_summary, initialValue)
        archivedListsHistorySizePref.onPreferenceChangeListener = this
    }

    private fun initPref(prefNameResId: Int) =
        preferenceScreen.findPreference(getString(prefNameResId))


    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return when (preference) {
            archivedListsHistorySizePref -> {
                EventLogger.logChangedMaxArchivedLists(newValue as String)
                archivedListsHistorySizePref.summary =
                        getString(R.string.max_archived_lists_summary, newValue)

                // Post this to the end of the queue so we only run after the property
                // has actually changed
                Handler().post { viewModel.ensureMaxArchivedLists() }
                true
            }
            else -> false
        }
    }
}