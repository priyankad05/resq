package com.example.loca

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class accelerometer : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var accelerometerValuesTextView: TextView
    private lateinit var alertDialog: AlertDialog
    private lateinit var timer: CountDownTimer
    private var isMildAccidentAcknowledged = false
    private lateinit var btn: ToggleButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.accelrometer)
        val userEmail=intent.getStringExtra("USER_EMAIL");
        accelerometerValuesTextView = findViewById(R.id.accelerometerValues)
        btn= findViewById(R.id.toggleDrivingMode)
        btn.setOnClickListener {
            val intent = Intent(this, Finish::class.java)
            startActivity(intent)
            finish()
        }
        btn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // If toggle is checked, open the second activity
                val intent = Intent(this, com.example.loca.accelerometer::class.java)
                intent.putExtra("USER_EMAIL", userEmail)
                startActivity(intent)
                btn.setBackgroundColor(Color.parseColor("#65B741"));
                btn.setTextColor(Color.WHITE);
            }
            else
            {
                btn.setBackgroundColor(Color.parseColor("#FF3131"));

            }
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        alertDialog = AlertDialog.Builder(this)
            .setTitle("Mild Accident Detected")
            .setMessage("Did Mild accident occur?")
            .setNegativeButton("No") { _, _ ->
                isMildAccidentAcknowledged = true
                val intent = Intent(this@accelerometer, this@accelerometer::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("USER_EMAIL",userEmail)
                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .create()

        timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                if (alertDialog.isShowing && !isMildAccidentAcknowledged) {
                    alertDialog.dismiss()
                    val intent = Intent(this@accelerometer, MapsActivity::class.java)
                    intent.putExtra("USER_EMAIL",userEmail)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val accelerometerText = "Accelerometer Values: \nX: $x\nY: $y\nZ: $z"
            accelerometerValuesTextView.text = accelerometerText
            val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble())

            if (acceleration > 0 && acceleration <= 20) {

            } else {
//                if (acceleration > 20 && acceleration <= 80) {
//                    Toast.makeText(this, "Medium chance of accident", Toast.LENGTH_SHORT).show()
//
//                } else if (acceleration > 80 && acceleration <= 100) {
//                    Toast.makeText(this, "Medium chance of accident", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "Severe chance of accident", Toast.LENGTH_SHORT).show()
//                }
                alertDialog.show()
                timer.start()

            }


        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy change if needed
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        timer.cancel()
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
}
