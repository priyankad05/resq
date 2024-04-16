

    package com.example.loca

    import android.annotation.SuppressLint
    import android.content.Intent
    import android.graphics.Color
    import android.net.Uri
    import android.os.Bundle
    import android.view.MenuItem
    import android.widget.Button
    import android.widget.ToggleButton
    import android.widget.VideoView
    import androidx.appcompat.app.AppCompatActivity
    import android.widget.MediaController
    import android.widget.TextView
    import androidx.fragment.app.Fragment

    import com.google.android.material.bottomnavigation.BottomNavigationView

    class DrivingMode : AppCompatActivity() {

        private lateinit var videoView: VideoView
        private lateinit var toggleDrivingMode: ToggleButton
        private lateinit var bottomNavigationView: BottomNavigationView



        @SuppressLint("MissingInflatedId")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.driving_mode)
            val userEmail = intent.getStringExtra("USER_EMAIL")

            videoView = findViewById(R.id.videoView)
            toggleDrivingMode = findViewById(R.id.toggleDrivingMode)

            // Set the video file from the raw folder
            val videoPath = "android.resource://" + packageName + "/" + R.raw.crash_video
            videoView.setVideoURI(Uri.parse(videoPath))

            // Initialize MediaController
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)

            // Start the video playback
            videoView.start()

            toggleDrivingMode.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // If toggle is checked, open the second activity
                    val intent = Intent(this, accelerometer::class.java)
                    intent.putExtra("USER_EMAIL", userEmail)
                    startActivity(intent)
                    toggleDrivingMode.setBackgroundColor(Color.parseColor("#65B741"));
                }
                else
                {
                    toggleDrivingMode.setBackgroundColor(Color.parseColor("#FF3131"));

                }
            }



        }
    }

