//AddFoodEntryActivity.kt
package com.example.fitnessapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.fitnessapp.databinding.ActivityAddFoodEntryBinding
import com.example.fitnessapp.models.FoodEntry
import com.example.fitnessapp.repositories.FitnessRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddFoodEntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFoodEntryBinding
    private lateinit var selectedDate: String

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun isDateInPast(dateString: String): Boolean {
        val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
        val currentDate = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time

        return selectedDate.before(currentDate)
    }


    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateCalories()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun updateCalories() {
        val protein = binding.proteinEditText.text.toString().toIntOrNull()
        val carbs = binding.carbsEditText.text.toString().toIntOrNull()
        val fat = binding.fatEditText.text.toString().toIntOrNull()

        val calories = (protein ?: 0) * 4 + (carbs ?: 0) * 4 + (fat ?: 0) * 9
        binding.caloriesTextView.text = "Calories: $calories"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = intent.getIntExtra("USER_ID", -1)
        selectedDate = intent.getStringExtra("SELECTED_DATE") ?: getCurrentDate()

        binding.proteinEditText.addTextChangedListener(textWatcher)
        binding.carbsEditText.addTextChangedListener(textWatcher)
        binding.fatEditText.addTextChangedListener(textWatcher)

        val repository = FitnessRepository(this)

        binding.saveFoodEntryButton.setOnClickListener {
            if (isDateInPast(selectedDate)) {
                Toast.makeText(this, "Adding a meal entry to a past date is not permitted.", Toast.LENGTH_SHORT).show()
            } else {
                val protein = binding.proteinEditText.text.toString().toIntOrNull()
                val carbs = binding.carbsEditText.text.toString().toIntOrNull()
                val fat = binding.fatEditText.text.toString().toIntOrNull()

                if (protein != null && carbs != null && fat != null) {
                    val calories = protein * 4 + carbs * 4 + fat * 9

                    val foodEntry = FoodEntry(
                        userId = userId,
                        calories = calories,
                        protein = protein,
                        carbs = carbs,
                        fat = fat,
                        date = selectedDate
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        val entryId = repository.addFoodEntry(foodEntry)
                        if (entryId != -1L) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@AddFoodEntryActivity,
                                    "Food entry added",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Start DashboardActivity
                                val dashboardIntent = Intent(this@AddFoodEntryActivity, DashboardActivity::class.java)
                                dashboardIntent.putExtra("USER_ID", userId)
                                dashboardIntent.putExtra("SELECTED_DATE", selectedDate)
                                setResult(RESULT_OK, dashboardIntent)
                                finish()

                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@AddFoodEntryActivity,
                                    "Failed to add food entry",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@AddFoodEntryActivity, "Please enter values for protein, carbs, and fat", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.backToDashboardButton.setOnClickListener {
            val dashboardIntent = Intent(this@AddFoodEntryActivity, DashboardActivity::class.java)
            dashboardIntent.putExtra("USER_ID", userId)
            dashboardIntent.putExtra("SELECTED_DATE", selectedDate)
            setResult(RESULT_CANCELED, dashboardIntent)
            finish()
        }


    }
}

