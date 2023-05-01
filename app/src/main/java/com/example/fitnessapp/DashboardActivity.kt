// DashboardActivity.kt  (hosts the nutrition info, meal log button, exercise log button, add food entry button, add workout button, logout button.
// Layout to this file is activity_dashboard.xml
package com.example.fitnessapp

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.fitnessapp.databinding.ActivityDashboardBinding
import com.example.fitnessapp.repositories.FitnessRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private var userId: Int = -1
    private lateinit var selectedDate: String

    companion object {
        const val MEAL_LOG_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addWorkoutButton.setOnClickListener {
            val intent = Intent(this, AddWorkoutActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("SELECTED_DATE", selectedDate)
            startActivity(intent)
        }

        selectedDate = intent.getStringExtra("SELECTED_DATE") ?: LocalDate.now().toString()

        binding.changeDateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    this.selectedDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    getCurrentDateAndUser()
                    loadDailyNutritionData(selectedDate)
                },
                LocalDate.now().year,
                LocalDate.now().monthValue - 1,
                LocalDate.now().dayOfMonth
            )
            datePickerDialog.show()
        }




        userId = intent.getIntExtra("USER_ID", -1)

        binding.addFoodEntryButton.setOnClickListener {
            val addFoodEntryIntent = Intent(this, AddFoodEntryActivity::class.java)
            addFoodEntryIntent.putExtra("USER_ID", userId)
            addFoodEntryIntent.putExtra("SELECTED_DATE", selectedDate)
            startActivity(addFoodEntryIntent)
        }





        binding.mealLogButton.setOnClickListener {
            val viewTodaysMealLogIntent = Intent(this, ViewTodaysMealLogActivity::class.java)
            viewTodaysMealLogIntent.putExtra("USER_ID", userId)
            viewTodaysMealLogIntent.putExtra("SELECTED_DATE", selectedDate.toString())
            startActivity(viewTodaysMealLogIntent)
        }



        binding.mealLogButton.setOnClickListener {
            val viewTodaysMealLogIntent = Intent(this, ViewTodaysMealLogActivity::class.java)
            viewTodaysMealLogIntent.putExtra("USER_ID", userId)
            viewTodaysMealLogIntent.putExtra("SELECTED_DATE", selectedDate.toString())
            startActivity(viewTodaysMealLogIntent)
        }


        binding.logoutButton.setOnClickListener {
            onLogoutButtonClick()
        }
        binding.exerciseLogButton.setOnClickListener {
            val viewTodaysExerciseLogIntent = Intent(this, ViewTodaysExerciseLogActivity::class.java)
            viewTodaysExerciseLogIntent.putExtra("USER_ID", userId)
            viewTodaysExerciseLogIntent.putExtra("SELECTED_DATE", selectedDate.toString())
            startActivity(viewTodaysExerciseLogIntent)
        }


    }



    private fun getCurrentDateAndUser() {
        val date = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = date.format(formatter)

        fitnessRepository.getUserById(userId).observe(this, { user ->
            val displayName = user?.username ?: "Unknown"
            binding.currentDateAndUser.text = "Date: $formattedDate, User: $displayName"
        })
    }



    private fun onLogoutButtonClick() {

        // Show a toast message
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()

        // Navigate back to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MEAL_LOG_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedDate = data?.getStringExtra("SELECTED_DATE") ?: LocalDate.now().toString()
            loadDailyNutritionData(LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        }
    }


    private val fitnessRepository = FitnessRepository(this)

    override fun onResume() {
        super.onResume()
        getCurrentDateAndUser()
        loadDailyNutritionData(LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
    }



    private fun loadDailyNutritionData(date: LocalDate = LocalDate.now()) {
        lifecycleScope.launch {
            val formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val nutritionData = fitnessRepository.getDailyNutritionData(userId, formattedDate)
            updateNutritionDataUI(nutritionData)
        }
    }


    private fun updateNutritionDataUI(nutritionData: FitnessRepository.NutritionData) {
        binding.totalCalories.text = "Total Calories: ${nutritionData.totalCalories}"
        binding.totalProtein.text = "Total Protein: ${nutritionData.totalProtein}g"
        binding.totalCarbohydrates.text = "Total Carbohydrates: ${nutritionData.totalCarbohydrates}g"
        binding.totalFat.text = "Total Fat: ${nutritionData.totalFat}g"
    }


}
