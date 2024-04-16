package com.example.loca

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.*

class Accident : AppCompatActivity() {
    private val SMS_PERMISSION_REQUEST_CODE = 123
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.accident)

        databaseReference = FirebaseDatabase.getInstance().reference
        val userEmail = intent.getStringExtra("USER_EMAIL")
        val address = intent.getStringExtra("ADDRESS")


        // Request SMS permission at runtime
        if (checkSmsPermission()) {
            fetchAndSendSms(userEmail, address)
        } else {
            requestSmsPermission()
        }
    }

    private fun fetchAndSendSms(userEmail: String?, address: String?) {
        var contact1: String? = null
        var contact2: String? = null

        if (userEmail != null) {
            val usersRef = databaseReference.child("users")

            usersRef.orderByChild("email").equalTo(userEmail)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val userName = userSnapshot.child("name").getValue(String::class.java)
                                val userContact = userSnapshot.child("userContact").getValue(String::class.java)
                                contact1 = userSnapshot.child("contact1").getValue(String::class.java)
                                contact2 = userSnapshot.child("contact2").getValue(String::class.java)
                            }

                            // Send SMS messages after fetching user data
                            sendSms(contact1, contact2, address)

                        } else {
                            Toast.makeText(this@Accident, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@Accident, "Database error. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(this@Accident, "User email is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSms(contact1: String?, contact2: String?, address: String?) {

        val smsManager: SmsManager = SmsManager.getDefault()
        val messege = "Hello, I have met an Accident.. Please reach and help me out!"
        smsManager.sendTextMessage(contact1, null, messege, null, null)
        smsManager.sendTextMessage(contact1, null, "My Location: "+ address, null, null)
        smsManager.sendTextMessage(contact2, null, messege, null, null)
        smsManager.sendTextMessage(contact2, null, "My Location: "+ address,null, null)
    }

    private fun checkSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.SEND_SMS),
                SMS_PERMISSION_REQUEST_CODE
            )
        }
    }
}
