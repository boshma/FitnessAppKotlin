// MainActivity.kt

package com.example.fitnessapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import com.example.fitnessapp.databinding.ActivityMainBinding
import com.example.fitnessapp.repositories.FitnessRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = FitnessRepository(this)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            lifecycleScope.launch {
                val user = repository.authenticateUser(username, password)
                if (user != null) {
                    // Login successful, navigate to another activity
                    Toast.makeText(this@MainActivity, "Logged in as $username", Toast.LENGTH_SHORT).show()
                    val dashboardIntent = Intent(this@MainActivity, DashboardActivity::class.java)
                    dashboardIntent.putExtra("USER_ID", user.id)
                    startActivity(dashboardIntent)
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.registerButton.setOnClickListener {
            // Navigate to RegisterActivity
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
