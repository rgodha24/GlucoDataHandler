package de.michelinside.glucodatahandler.common.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import de.michelinside.glucodatahandler.common.GlucoDataService
import de.michelinside.glucodatahandler.common.R

enum class ChannelType(val channelId: String, val nameResId: Int, val descrResId: Int, val importance: Int = NotificationManager.IMPORTANCE_DEFAULT) {
    MOBILE_FOREGROUND("GlucoDataNotify_foreground", R.string.mobile_foreground_notification_name, R.string.mobile_foreground_notification_descr ),
    MOBILE_SECOND("GlucoDataNotify_permanent", R.string.mobile_second_notification_name, R.string.mobile_second_notification_descr ),
    WORKER("worker_notification_01", R.string.worker_notification_name, R.string.worker_notification_descr, NotificationManager.IMPORTANCE_LOW ),
    WEAR_FOREGROUND("glucodatahandler_service_01", R.string.wear_foreground_notification_name, R.string.wear_foreground_notification_descr, NotificationManager.IMPORTANCE_LOW),
    ANDROID_AUTO("GlucoDataNotify_Car", R.string.android_auto_notification_name, R.string.android_auto_notification_descr ),
    ANDROID_AUTO_FOREGROUND("GlucoDataAuto_foreground", R.string.mobile_foreground_notification_name, R.string.mobile_foreground_notification_descr, NotificationManager.IMPORTANCE_LOW ),
    VERY_LOW_ALARM("very_low_alarm", R.string.very_low_alarm_notification_name, R.string.very_low_alarm_notification_descr, NotificationManager.IMPORTANCE_MAX ),
    LOW_ALARM("low_alarm", R.string.low_alarm_notification_name, R.string.low_alarm_notification_descr, NotificationManager.IMPORTANCE_HIGH ),
    HIGH_ALARM("high_alarm", R.string.high_alarm_notification_name, R.string.high_alarm_notification_descr, NotificationManager.IMPORTANCE_HIGH ),
    VERY_HIGH_ALARM("very_high_alarm", R.string.very_high_alarm_notification_name, R.string.very_high_alarm_notification_descr, NotificationManager.IMPORTANCE_MAX ),
    OBSOLETE_ALARM("obsolete_alarm", R.string.obsolete_alarm_notification_name, R.string.obsolete_alarm_notification_descr, NotificationManager.IMPORTANCE_HIGH );
}
object Channels {
    private var notificationMgr: NotificationManager? = null

    fun getNotificationManager(context: Context? = null): NotificationManager {
        if (notificationMgr == null) {
            notificationMgr = if (context != null)
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            else
                GlucoDataService.context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationMgr!!
    }

    fun getNotificationChannel(context: Context, type: ChannelType, silent: Boolean = true): NotificationChannel {
        val notificationChannel = NotificationChannel(
            type.channelId,
            context.getString(type.nameResId),
            type.importance
        )
        notificationChannel.description = context.getString(type.descrResId)
        if (silent)
            notificationChannel.setSound(null, null)
        return notificationChannel
    }

    fun createNotificationChannel(context: Context, type: ChannelType, silent: Boolean = true) {
        getNotificationManager(context).createNotificationChannel(getNotificationChannel(context, type, silent))
    }

    fun deleteNotificationChannel(context: Context, type: ChannelType) {
        getNotificationManager(context).deleteNotificationChannel(type.channelId)
    }
}