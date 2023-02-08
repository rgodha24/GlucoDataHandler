package de.michelinside.glucodatahandler

import de.michelinside.glucodatahandler.common.ReceiveData
import de.michelinside.glucodatahandler.common.ReceiveDataInterface
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import de.michelinside.glucodatahandler.common.Constants
import de.michelinside.glucodatahandler.common.ReceiveDataSource
import de.michelinside.glucodatahandler.databinding.ActivityWaerBinding


class WaerActivity : Activity(), ReceiveDataInterface {

    private val LOG_ID = "GlucoDataHandler.Main"
    private lateinit var binding: ActivityWaerBinding
    private lateinit var txtBgValue: TextView
    private lateinit var txtVersion: TextView
    private lateinit var txtValueInfo: TextView
    private lateinit var txtConnInfo: TextView
    private lateinit var viewIcon: ImageView
    private lateinit var numMin: EditText
    private lateinit var numMax: EditText
    private lateinit var switchForground: Switch
    private lateinit var switchNotifcation: Switch
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        try {

            super.onCreate(savedInstanceState)

            binding = ActivityWaerBinding.inflate(layoutInflater)
            setContentView(binding.root)

            txtVersion = findViewById(R.id.txtVersion)
            txtVersion.text = BuildConfig.VERSION_NAME

            sharedPref = this.getSharedPreferences(Constants.SHARED_PREF_TAG, Context.MODE_PRIVATE)
            numMin = findViewById(R.id.numMin)
            numMax = findViewById(R.id.numMax)

            numMin.addTextChangedListener(EditTargetChanger(true, this))
            numMax.addTextChangedListener(EditTargetChanger(false, this))

            numMin.setText(sharedPref.getFloat(Constants.SHARED_PREF_TARGET_MIN, ReceiveData.targetMin).toString())
            numMax.setText(sharedPref.getFloat(Constants.SHARED_PREF_TARGET_MAX, ReceiveData.targetMax).toString())

            switchForground = findViewById(R.id.switchForground)
            switchForground.isChecked = sharedPref.getBoolean(Constants.SHARED_PREF_FOREGROUND_SERVICE, false)
            switchForground.setOnCheckedChangeListener { _, isChecked ->
                Log.d(LOG_ID, "Foreground service changed: " + isChecked.toString())
                try {
                    with (sharedPref.edit()) {
                        putBoolean(Constants.SHARED_PREF_FOREGROUND_SERVICE, isChecked)
                        apply()
                    }
                    val serviceIntent = Intent(this, GlucoDataServiceWear::class.java)
                    if (isChecked)
                        serviceIntent.putExtra(Constants.SHARED_PREF_FOREGROUND_SERVICE, true)
                    else
                        serviceIntent.putExtra(Constants.ACTION_STOP_FOREGROUND, true)
                    this.startService(serviceIntent)
                } catch (exc: Exception) {
                    Log.e(LOG_ID, "Changing foreground service exception: " + exc.message.toString() )
                }
            }

            switchNotifcation = findViewById(R.id.switchNotifcation)
            switchNotifcation.isChecked = sharedPref.getBoolean(Constants.SHARED_PREF_NOTIFICATION, false)
            switchNotifcation.setOnCheckedChangeListener { _, isChecked ->
                Log.d(LOG_ID, "Notification changed: " + isChecked.toString())
                try {
                    with (sharedPref.edit()) {
                        putBoolean(Constants.SHARED_PREF_NOTIFICATION, isChecked)
                        apply()
                    }
                } catch (exc: Exception) {
                    Log.e(LOG_ID, "Changing notification exception: " + exc.message.toString() )
                }
            }

            GlucoDataServiceWear.start(this)
        } catch( exc: Exception ) {
            Log.e(LOG_ID, exc.message + "\n" + exc.stackTraceToString())
        }
    }

    override fun onPause() {
        try {
            super.onPause()
            ReceiveData.remNotifier(this)
            Log.d(LOG_ID, "onPause called")
        } catch( exc: Exception ) {
            Log.e(LOG_ID, exc.message + "\n" + exc.stackTraceToString())
        }
    }

    override fun onResume() {
        try {
            super.onResume()
            Log.d(LOG_ID, "onResume called")
            update()
            ReceiveData.addNotifier(this)
        } catch( exc: Exception ) {
            Log.e(LOG_ID, exc.message + "\n" + exc.stackTraceToString())
        }
    }

    private fun update() {
        if(ReceiveData.time > 0) {
            txtBgValue = findViewById(R.id.txtBgValue)
            txtBgValue.text = ReceiveData.getClucoseAsString()
            txtBgValue.setTextColor(ReceiveData.getClucoseColor())
            viewIcon = findViewById(R.id.viewIcon)
            viewIcon.setImageIcon(ReceiveData.getArrowIcon(this))
            txtValueInfo = findViewById(R.id.txtValueInfo)
            txtValueInfo.text = ReceiveData.getAsString(this, false)
            txtConnInfo = findViewById(R.id.txtConnInfo)
            if (ReceiveData.capabilityInfo != null && ReceiveData.capabilityInfo!!.nodes.size > 0)
                txtConnInfo.text = resources.getText(R.string.activity_connected_label)
            else
                txtConnInfo.text = resources.getText(R.string.activity_disconnected_label)
        }
    }

    override fun OnReceiveData(context: Context, dataSource: ReceiveDataSource, extras: Bundle?) {
        Log.d(LOG_ID, "new intent received from: " + dataSource.toString())
        update()
    }
}

class EditTargetChanger(minTarget: Boolean, ctx: Context): TextWatcher {
    private val context = ctx
    private val min = minTarget
    private val LOG_ID = "GlucoDataHandler.EditChangeWatch"

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        Log.d(LOG_ID, "onTextChanged: s=" + s + " start=" + start.toString() + " before=" + before.toString() + " count=" + count.toString())
        try {
            ReceiveData.updateTarget(context, min, s.toString().toFloat())
        } catch (exc: Exception) {
            Log.e(LOG_ID, "Changing target exception: " + exc.message.toString() )
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

}