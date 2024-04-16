package com.example.loca
import android.content.Intent
import android.os.Bundle
import android.widget.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loca.databinding.ActivitySignInBinding
import com.google.firebase.database.*

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference

        val emailEditText = findViewById<EditText>(R.id.emailEt)
        val passwordEditText = findViewById<EditText>(R.id.passET)

        val loginButton = findViewById<Button>(R.id.button)
        loginButton.setOnClickListener {
            // Get the values entered by the user
            val enteredEmail = emailEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()

            // Call the function to perform sign-in
            signInUser(enteredEmail, enteredPassword)
        }
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signInUser(email: String, password: String) {
        // Reference to the "users" node in the database
        val usersRef = databaseReference.child("users")

        // Attach a listener to read the data
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // User exists, check password
                    for (userSnapshot in snapshot.children) {
                        val storedPassword = userSnapshot.child("password").getValue(String::class.java)
                        val storedUserContact = userSnapshot.child("userContact").getValue(String::class.java)

                        if (password == storedPassword) {

                            Toast.makeText(this@SignInActivity, "Login Sucessfull !", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@SignInActivity, DrivingMode::class.java)
                            intent.putExtra("USER_EMAIL", email)
                            startActivity(intent)

                            finish()
                        } else {
                            // Passwords don't match, show a toast
                            Toast.makeText(this@SignInActivity, "Incorrect password", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // User with the entered email not found, show a toast
                    Toast.makeText(this@SignInActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(this@SignInActivity, "Database error. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
