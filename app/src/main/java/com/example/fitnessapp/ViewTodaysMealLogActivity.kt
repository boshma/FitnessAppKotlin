//ViewTodaysMealLogActivity.kt (layout to this file is activity_view_todays_meal_log.xml)
package com.example.fitnessapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fitnessapp.controllers.FoodEntryAdapter
import com.example.fitnessapp.databinding.ActivityViewTodaysMealLogBinding
import com.example.fitnessapp.repositories.FitnessRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.widget.Toast

class ViewTodaysMealLogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewTodaysMealLogBinding
    private var userId: Int = -1
    private var selectedDate: String? = null

    companion object {
        const val ADD_FOOD_ENTRY_REQUEST_CODE = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewTodaysMealLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra("USER_ID", -1)
        selectedDate = intent.getStringExtra("SELECTED_DATE") ?: getCurrentDate()

        // Add click listener for the add food entry button
        binding.addFoodEntryButton.setOnClickListener {
            val addFoodEntryIntent = Intent(this, AddFoodEntryActivity::class.java)
            addFoodEntryIntent.putExtra("USER_ID", userId)
            addFoodEntryIntent.putExtra("SELECTED_DATE", selectedDate)
            startActivityForResult(addFoodEntryIntent, ADD_FOOD_ENTRY_REQUEST_CODE)
        }


        // Add click listener for the back to dashboard button
        binding.backToDashboardButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra("SELECTED_DATE", selectedDate)
            setResult(RESULT_OK, intent)
            finish()
        }


        loadMealLogData(userId)
    }

    private fun loadMealLogData(userId: Int = -1) {
        val userToLoad = if (userId != -1) userId else 1

        val repository = FitnessRepository(this)

        CoroutineScope(Dispatchers.IO).launch {
            val foodEntries = repository.getFoodEntriesByDate(userToLoad, selectedDate!!)

            withContext(Dispatchers.Main) {
                val foodEntryAdapter = FoodEntryAdapter(
                    this@ViewTodaysMealLogActivity,
                    foodEntries,
                    onDeleteClickListener = { position ->
                        onFoodEntryDeleted(position)
                    }
                )
                binding.foodEntryListView.adapter = foodEntryAdapter
            }
        }
    }




    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun onFoodEntryDeleted(position: Int) {
        val foodEntryAdapter = binding.foodEntryListView.adapter as FoodEntryAdapter
        val foodEntry = foodEntryAdapter.getItem(position)

        CoroutineScope(Dispatchers.IO).launch {
            val repository = FitnessRepository(this@ViewTodaysMealLogActivity)
            if (foodEntry != null) {
                repository.deleteFoodEntry(foodEntry.id)
            }
            withContext(Dispatchers.Main) {
                // Remove the deleted food entry from the data source
                foodEntryAdapter.remove(foodEntry)
                // Notify the adapter that the data has changed
                foodEntryAdapter.notifyDataSetChanged()
                // Notify the Dashboard to update total calories
                setResult(RESULT_OK, Intent().putExtra("DATA_UPDATED", true))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_FOOD_ENTRY_REQUEST_CODE && resultCode == RESULT_OK) {
            loadMealLogData(userId)
        }
    }
}



