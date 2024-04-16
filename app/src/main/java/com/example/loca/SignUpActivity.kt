package com.example.loca

import User
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loca.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference

        binding.button.setOnClickListener {
            // Get input values
            val email = binding.emailEt.text.toString()
            val userContact = binding.userContactEt.text.toString()
            val contact1 = binding.contact1Et.text.toString()
            val contact2 = binding.contact2Et.text.toString()
            val password = binding.passET.text.toString()
            val confirmPassword = binding.confirmPassEt.text.toString()

            // Check if any field is empty
            if (email.isEmpty() || userContact.isEmpty() || contact1.isEmpty() ||
                contact2.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Validate contact lengths, email format, and password match
                if (userContact.length == 10 && contact1.length == 10 && contact2.length == 10 &&
                    (email.endsWith("@gmail.com") || email.endsWith("@bmsce.ac.in")) &&
                    (password == confirmPassword)
                ) {
                    // Create a User object
                    val user = User(email, userContact, contact1, contact2, password)

                    // Write user data to the database
                    writeUserData(user)

                    Toast.makeText(this, "Registration successful! ", Toast.LENGTH_LONG).show()

                    // Navigate to MapsActivity
                    navigateToMapsActivity(email)
                } else {
                    // Handle invalid input scenarios
                    // ...
                }
            }
        }

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun writeUserData(user: User) {
        // Generate a unique key for the user
        val userId = databaseReference.child("users").push().key

        // Write user data to the database under the "users" node with the generated key
        databaseReference.child("users").child(userId!!).setValue(user)
    }

    private fun navigateToMapsActivity(email: String) {
        // Create an intent to navigate to DrivingMode activity
        val intent = Intent(this, DrivingMode::class.java)

        // Pass the email as an extra with the intent
        intent.putExtra("USER_EMAIL", email)

        // Start the DrivingMode activity
        startActivity(intent)
    }

}
