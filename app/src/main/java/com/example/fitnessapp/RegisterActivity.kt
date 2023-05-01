// RegisterActivity.kt
package com.example.fitnessapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fitnessapp.databinding.ActivityRegisterBinding
import com.example.fitnessapp.repositories.FitnessRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = FitnessRepository(this)

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    lifecycleScope.launch {
                        val existingUser = repository.authenticateUser(username, password)
                        if (existingUser == null) {
                            val userId = repository.createUser(username, password)
                            if (userId != -1L) {
                                Toast.makeText(this@RegisterActivity, "Registered as $username", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                                //finish()
                            } else {
                                Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@RegisterActivity, "User already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
