//ViewTodaysExerciseLogActivity.kt (layout file is activity_view_todays_exercise_log.xml)

package com.example.fitnessapp

import java.text.SimpleDateFormat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnessapp.databinding.ActivityViewTodaysExerciseLogBinding
import com.example.fitnessapp.repositories.FitnessRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import com.example.fitnessapp.controllers.AddedExercisesAdapter
import com.example.fitnessapp.models.Exercise

class ViewTodaysExerciseLogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewTodaysExerciseLogBinding
    private var userId: Int = -1
    private lateinit var selectedDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewTodaysExerciseLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backToDashboardButton.setOnClickListener {
            onBackPressed()
        }
        selectedDate = intent.getStringExtra("SELECTED_DATE") ?: getCurrentDate()

        userId = intent.getIntExtra("USER_ID", -1)
    }

    override fun onResume() {
        super.onResume()
        loadExerciseLogData(userId)
    }
    private fun deleteExercise(exercise: Exercise) {
        val repository = FitnessRepository(this)

        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteExercise(exercise.id)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@ViewTodaysExerciseLogActivity, "Exercise deleted", Toast.LENGTH_SHORT).show()
                loadExerciseLogData(userId)
            }
        }
    }




    private fun loadExerciseLogData(userId: Int = -1) {
        val userToLoad = if (userId != -1) userId else 1

        val repository = FitnessRepository(this)

        CoroutineScope(Dispatchers.IO).launch {
            val exercises = repository.getExercisesByDate(userToLoad, selectedDate)

            withContext(Dispatchers.Main) {
                val exerciseAdapter = AddedExercisesAdapter(exercises) { exercise ->
                    deleteExercise(exercise)
                }
                binding.exerciseRecyclerView.layoutManager = LinearLayoutManager(this@ViewTodaysExerciseLogActivity)
                binding.exerciseRecyclerView.adapter = exerciseAdapter
            }
        }
    }



    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}