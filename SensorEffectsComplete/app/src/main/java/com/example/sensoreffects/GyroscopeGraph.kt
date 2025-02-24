package com.example.sensoreffects

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.data.Entry

@Composable
fun GyroscopeGraph(context: Context) {
    val points = 100
    val time = 10f

    //* is the spread operator, lets us do something for every element in the Array
    val rollHistory = remember { mutableStateListOf(*Array(points) { i -> Entry(-time + (i * (time / points)), 0f) }) }
    val pitchHistory = remember { mutableStateListOf(*Array(points) { i -> Entry(-time + (i * (time / points)), 0f) }) }
    val yawHistory = remember { mutableStateListOf(*Array(points) { i -> Entry(-time + (i * (time / points)), 0f) }) }

    var timeCounter by remember { mutableFloatStateOf(0f) } // X-axis time tracker

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val gyroscope = remember { sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) }

    var roll by remember { mutableFloatStateOf(0f) }
    var pitch by remember { mutableFloatStateOf(0f) }
    var yaw by remember { mutableFloatStateOf(0f) }

    var alertMessage by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        var startTime = System.currentTimeMillis()

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if(System.currentTimeMillis() - startTime >= (time/points) * 1000) {
                        startTime = System.currentTimeMillis()
                        roll = it.values[0]
                        pitch = it.values[1]
                        yaw = it.values[2]

                        // If we have too many data points, remove the oldest one
                        if (rollHistory.size > points) rollHistory.removeAt(0)
                        if (pitchHistory.size > points) pitchHistory.removeAt(0)
                        if (yawHistory.size > points) yawHistory.removeAt(0)

                        rollHistory.add(Entry(timeCounter, roll))
                        pitchHistory.add(Entry(timeCounter, pitch))
                        yawHistory.add(Entry(timeCounter, yaw))

                        timeCounter += time / points // Increment time
                    }
                }
            }

            //Only needed for the abstract class
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        gyroscope?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    // LaunchedEffect to monitor for threshold breaches
    LaunchedEffect(roll, pitch, yaw) {
        val threshold = 0.3f
        when {
            roll > threshold || roll < -threshold -> {
                alertMessage = "Roll limit exceeded!"
                vibrateDevice(context)
            }
            pitch > threshold || pitch < -threshold -> {
                alertMessage = "Pitch limit exceeded!"
                vibrateDevice(context)
            }
            yaw > threshold || yaw < -threshold -> {
                alertMessage = "Yaw limit exceeded!"
                vibrateDevice(context)
            }
            else -> {
                alertMessage = ""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Gyroscope Live Graph", style = MaterialTheme.typography.titleLarge)

        if (alertMessage.isNotEmpty()) {
            Text(text = alertMessage, color = Color.Red, modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LiveChart("Roll", rollHistory, Color.Red)
        LiveChart("Pitch", pitchHistory, Color.Green)
        LiveChart("Yaw", yawHistory, Color.Blue)
    }
}

// Function to handle device vibration
private fun vibrateDevice(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(300)
    }
}