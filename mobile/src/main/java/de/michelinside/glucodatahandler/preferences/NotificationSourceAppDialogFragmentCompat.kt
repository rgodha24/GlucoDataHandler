package de.michelinside.glucodatahandler.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import androidx.preference.PreferenceDialogFragmentCompat
import de.michelinside.glucodatahandler.R

class NotificationSourceAppDialogFragmentCompat : PreferenceDialogFragmentCompat() {
    private var selectedPackage = ""
    private var appChooserPreference: NotificationSourceAppPreference? = null

    companion object {
        fun initial(key: String): NotificationSourceAppDialogFragmentCompat {
            val fragment = NotificationSourceAppDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }

        @SuppressLint("QueryPermissionsNeeded")
        private fun getInstalledApps(context: Context): HashMap<String, String> {
            val pm = context.packageManager
            val apps = HashMap<String, String>()

            pm.getInstalledApplications(PackageManager.GET_META_DATA).forEach { appInfo ->
                apps[appInfo.packageName] = pm.getApplicationLabel(appInfo).toString()
            }
            return apps
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appChooserPreference = preference as NotificationSourceAppPreference
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        selectedPackage = appChooserPreference!!.getSelectedPackage()

        val radioGroup = view.findViewById<RadioGroup>(R.id.notificationSourceAppRadioGroup)
        val scrollView = view.findViewById<ScrollView>(R.id.notificationSourceAppScrollView)

        createAppList(radioGroup)

        if (radioGroup.childCount > 10) {
            scrollView.layoutParams.height = resources.displayMetrics.heightPixels / 2
        }
    }

    private fun createAppList(group: RadioGroup) {
        val apps = getInstalledApps(requireContext())
            .toList()
            .sortedBy { (_, value) -> value.lowercase() }
            .toMap()

        var selectedButton: RadioButton? = null

        apps.forEach { (packageName, appName) ->
            val radioButton = RadioButton(requireContext()).apply {
                text = appName
                tag = packageName
                if (packageName == selectedPackage) {
                    selectedButton = this
                }
                setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        selectedPackage = button.tag as String
                    }
                }
            }
            group.addView(radioButton)
        }

        selectedButton?.isChecked = true
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            appChooserPreference!!.setSelectedPackage(selectedPackage)
        }
    }
}
