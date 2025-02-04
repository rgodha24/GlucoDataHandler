package de.michelinside.glucodatahandler.common.chart

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import de.michelinside.glucodatahandler.common.Constants
import de.michelinside.glucodatahandler.common.database.dbAccess
import de.michelinside.glucodatahandler.common.notifier.InternalNotifier
import de.michelinside.glucodatahandler.common.notifier.NotifySource

class ChartBitmapCreator(chart: GlucoseChart, context: Context, durationPref: String = "", private val forComplication: Boolean = false, private val showAxis: Boolean = false): ChartCreator(chart, context, durationPref) {
    private val LOG_ID = "GDH.Chart.BitmapCreator"
    private var bitmap: Bitmap? = null
    override val resetChart = true
    override val circleRadius = 3F
    override var durationHours = 2
    override val touchEnabled = false

    override fun initXaxis() {
        Log.v(LOG_ID, "initXaxis")
        chart.xAxis.isEnabled = showAxis
        if(chart.xAxis.isEnabled)
            super.initXaxis()
    }

    override fun initYaxis() {
        Log.v(LOG_ID, "initYaxis")
        if(showAxis) {
            super.initYaxis()
        } else {
            chart.axisRight.setDrawAxisLine(false)
            chart.axisRight.setDrawLabels(false)
            chart.axisRight.setDrawZeroLine(false)
            chart.axisRight.setDrawGridLines(false)
            chart.axisLeft.isEnabled = false
        }
    }

    override fun initChart() {
        super.initChart()
        chart.isDrawingCacheEnabled = false
    }

    override fun getMaxRange(): Long {
        return getDefaultRange()
    }

    override fun getDefaultMaxValue(): Float {
        if(forComplication)
            return maxOf(super.getDefaultMaxValue(), 310F)
        return super.getDefaultMaxValue()
    }

    override fun updateChart(dataSet: LineDataSet?) {
        if(dataSet != null) {
            Log.v(LOG_ID, "Update chart for ${dataSet.values.size} entries and ${dataSet.circleColors.size} colors")
            if(dataSet.values.isNotEmpty())
                chart.data = LineData(dataSet)
            else
                chart.data = LineData()
        }
        addEmptyTimeData()
        chart.notifyDataSetChanged()
        bitmap = null  // reset
        chart.postInvalidate()
        InternalNotifier.notify(context, NotifySource.GRAPH_CHANGED, Bundle().apply { putInt(Constants.GRAPH_ID, chart.id) })
    }

    override fun updateTimeElapsed() {
        update(dbAccess.getGlucoseValues(getMinTime()))
    }

    fun getBitmap(): Bitmap? {
        if(bitmap == null)
            bitmap = createBitmap()
        return bitmap
    }
}