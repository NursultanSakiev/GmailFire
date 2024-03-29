package com.example.gmailfire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.gmailfire.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException


class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var sendCodeButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        sendCodeButton = findViewById(R.id.sendCodeButton)

        auth = FirebaseAuth.getInstance()

        sendCodeButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendVerificationEmail(email)
        }
    }

    private fun sendVerificationEmail(email: String) {
        auth.createUserWithEmailAndPassword(email, "randomPassword")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Verification email sent to $email",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to send verification email",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    try {
                        throw task.exception!!

                    } catch (e: FirebaseAuthInvalidUserException) {
                        Toast.makeText(
                            this,
                            "User with this email already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}