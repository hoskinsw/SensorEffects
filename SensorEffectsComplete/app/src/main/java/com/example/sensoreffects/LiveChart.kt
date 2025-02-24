package com.example.sensoreffects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun LiveChart(title: String, data: List<Entry>, lineColor: Color) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description = Description().apply { text = title }
                axisRight.isEnabled = false
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(false)
                xAxis.setDrawLabels(false)
                axisLeft.textColor = android.graphics.Color.WHITE
                xAxis.textColor = android.graphics.Color.WHITE
            }
        },
        update = { chart ->
            val dataset = LineDataSet(data, title).apply {
                color = lineColor.hashCode()
                valueTextColor = android.graphics.Color.WHITE
                setDrawCircles(false)
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth curve
                lineWidth = 2f
            }

            chart.data = LineData(dataset)
            chart.invalidate() // Refresh chart
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(Color.DarkGray)
    )
}