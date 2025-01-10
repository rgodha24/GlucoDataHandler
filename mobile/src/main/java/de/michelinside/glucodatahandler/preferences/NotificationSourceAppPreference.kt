package de.michelinside.glucodatahandler.preferences

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import de.michelinside.glucodatahandler.R

class NotificationSourceAppPreference : DialogPreference {
    private var selectedPackage = ""

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init()
    }

    private fun init() {
        setSelectedPackage("", false)
    }

    override fun getDialogLayoutResource(): Int {
        return R.layout.notification_select_receiver
    }

    fun getSelectedPackage(): String {
        return selectedPackage
    }

    fun setSelectedPackage(newPackage: String, save: Boolean = true) {
        selectedPackage = newPackage
        if(save) {
            persistString(selectedPackage)
        }
        summary = selectedPackage
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return context.packageName
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        setSelectedPackage(
            if (restorePersistedValue || defaultValue == null)
                getPersistedString(selectedPackage)
            else
                defaultValue as String
        )
    }
}
