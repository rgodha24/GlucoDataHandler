package de.michelinside.glucodatahandler

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import de.michelinside.glucodatahandler.common.Constants
import de.michelinside.glucodatahandler.common.ReceiveData
import de.michelinside.glucodatahandler.common.WearPhoneConnection
import de.michelinside.glucodatahandler.common.notifier.InternalNotifier
import de.michelinside.glucodatahandler.common.notifier.NotifierInterface
import de.michelinside.glucodatahandler.common.notifier.NotifyDataSource


class MainActivity : AppCompatActivity(), NotifierInterface {
    private lateinit var txtBgValue: TextView
    private lateinit var viewIcon: ImageView
    private lateinit var txtLastValue: TextView
    private lateinit var txtVersion: TextView
    private lateinit var txtWearInfo: TextView
    private lateinit var txtCarInfo: TextView
    private lateinit var sharedPref: SharedPreferences
    private val LOG_ID = "GlucoDataHandler.Main"

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            Log.d(LOG_ID, "onCreate called")
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }

            GlucoDataServiceMobile.start(this)


            txtBgValue = findViewById(R.id.txtBgValue)
            viewIcon = findViewById(R.id.viewIcon)
            txtLastValue = findViewById(R.id.txtLastValue)
            txtWearInfo = findViewById(R.id.txtWearInfo)
            txtCarInfo = findViewById(R.id.txtCarInfo)

            PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
            sharedPref = this.getSharedPreferences(Constants.SHARED_PREF_TAG, Context.MODE_PRIVATE)

            ReceiveData.initData(this)

            txtVersion = findViewById(R.id.txtVersion)
            txtVersion.text = BuildConfig.VERSION_NAME

            val sendToAod = sharedPref.getBoolean(Constants.SHARED_PREF_SEND_TO_GLUCODATA_AOD, false)

            if(!sharedPref.contains(Constants.SHARED_PREF_GLUCODATA_RECEIVERS)) {
                val receivers = HashSet<String>()
                if (sendToAod)
                    receivers.add("de.metalgearsonic.glucodata.aod")
                Log.i(LOG_ID, "Upgrade receivers to " + receivers.toString())
                with(sharedPref.edit()) {
                    putStringSet(Constants.SHARED_PREF_GLUCODATA_RECEIVERS, receivers)
                    apply()
                }
            }
        } catch (exc: Exception) {
            Log.e(LOG_ID, "onCreate exception: " + exc.message.toString() )
        }
    }

    override fun onPause() {
        try {
            super.onPause()
            InternalNotifier.remNotifier(this)
            Log.d(LOG_ID, "onPause called")
        } catch (exc: Exception) {
            Log.e(LOG_ID, "onPause exception: " + exc.message.toString() )
        }
    }

    override fun onResume() {
        try {
            super.onResume()
            Log.d(LOG_ID, "onResume called")
            update()
            InternalNotifier.addNotifier(this, mutableSetOf(
                NotifyDataSource.BROADCAST,
                NotifyDataSource.MESSAGECLIENT,
                NotifyDataSource.CAPILITY_INFO,
                NotifyDataSource.NODE_BATTERY_LEVEL,
                NotifyDataSource.SETTINGS,
                NotifyDataSource.CAR_CONNECTION,
                NotifyDataSource.OBSOLETE_VALUE))
        } catch (exc: Exception) {
            Log.e(LOG_ID, "onResume exception: " + exc.message.toString() )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        try {
            Log.d(LOG_ID, "onCreateOptionsMenu called")
            val inflater = menuInflater
            inflater.inflate(R.menu.menu_items, menu)
            return true
        } catch (exc: Exception) {
            Log.e(LOG_ID, "onCreateOptionsMenu exception: " + exc.message.toString() )
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {
            Log.d(LOG_ID, "onOptionsItemSelected for " + item.itemId.toString())
            if (item.itemId == R.id.action_settings) {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            } else if (item.itemId == R.id.action_help) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getText(R.string.help_link).toString()))
                startActivity(browserIntent)
                return true
            }
        } catch (exc: Exception) {
            Log.e(LOG_ID, "onOptionsItemSelected exception: " + exc.message.toString() )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun update() {
        try {
            Log.d(LOG_ID, "update values")
            txtBgValue.text = ReceiveData.getClucoseAsString()
            txtBgValue.setTextColor(ReceiveData.getClucoseColor())
            if (ReceiveData.isObsolete(Constants.VALUE_OBSOLETE_SHORT_SEC) && !ReceiveData.isObsolete()) {
                txtBgValue.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                txtBgValue.paintFlags = 0
            }
            viewIcon.setImageIcon(ReceiveData.getArrowIcon())
            txtLastValue.text = ReceiveData.getAsString(this)
            if (WearPhoneConnection.nodesConnected) {
                txtWearInfo.text = String.format(resources.getText(R.string.activity_main_connected_label).toString(), WearPhoneConnection.getBatterLevelsAsString())
            }
            else
                txtWearInfo.text = resources.getText(R.string.activity_main_disconnected_label)
            txtCarInfo.text = if (CarModeReceiver.connected) resources.getText(R.string.activity_main_car_connected_label) else resources.getText(R.string.activity_main_car_disconnected_label)
        } catch (exc: Exception) {
            Log.e(LOG_ID, "update exception: " + exc.message.toString() )
        }
    }

    override fun OnNotifyData(context: Context, dataSource: NotifyDataSource, extras: Bundle?) {
        Log.d(LOG_ID, "new intent received")
        update()
    }
}