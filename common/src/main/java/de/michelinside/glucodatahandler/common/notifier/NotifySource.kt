package de.michelinside.glucodatahandler.common.notifier

enum class NotifySource {
    BROADCAST,
    MESSAGECLIENT,
    CAPILITY_INFO,
    BATTERY_LEVEL,
    NODE_BATTERY_LEVEL,
    SETTINGS,
    CAR_CONNECTION,
    OBSOLETE_VALUE,
    TIME_VALUE,
    SOURCE_SETTINGS,
    SOURCE_STATE_CHANGE,
    TIME_NOTIFIER_CHANGE,
    IOB_COB_CHANGE,
    IOB_COB_TIME,
    LOGCAT_REQUEST,
    PATIENT_DATA_CHANGED,
    ALARM_TRIGGER,
    OBSOLETE_ALARM_TRIGGER,
    DELTA_ALARM_TRIGGER,
    ALARM_SETTINGS,
    ALARM_STATE_CHANGED,
    NOTIFICATION_STOPPED,
    COMMAND,
    NEW_VERSION_AVAILABLE,
    TTS_STATE_CHANGED,
    DISPLAY_STATE_CHANGED;
}