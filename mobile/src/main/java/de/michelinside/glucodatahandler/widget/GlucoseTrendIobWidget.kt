package de.michelinside.glucodatahandler.widget

import de.michelinside.glucodatahandler.R

class GlucoseTrendIobWidget : GlucoseBaseWidget(WidgetType.GLUCOSE_TREND_IOB, hasTrend = true, hasIobCob = true) {
    override fun getLayout(): Int = R.layout.glucose_trend_iob_widget_long
    override fun getLongLayout(): Int = R.layout.glucose_trend_iob_widget_long
}
