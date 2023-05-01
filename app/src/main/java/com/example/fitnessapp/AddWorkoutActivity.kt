// AddWorkoutActivity.kt (here you can add a new workout to the dropdown for add a workout to your log.)
package com.example.fitnessapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fitnessapp.databinding.ActivityAddWorkoutBinding
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnessapp.models.Exercise
import com.example.fitnessapp.SharedPrefUtil
import com.example.fitnessapp.controllers.AddedExercisesAdapter
import com.example.fitnessapp.repositories.FitnessRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.fitnessapp.models.User
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddWorkoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddWorkoutBinding
    private lateinit var selectedDate: String

    private fun removeExercise(exercise: Exercise, exercises: MutableList<Exercise>) {
        exercises.remove(exercise)
        (binding.addedExercisesRecyclerView.adapter as AddedExercisesAdapter).notifyDataSetChanged()
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


    private fun createAddedExercisesAdapter(addedExercises: MutableList<Exercise>): AddedExercisesAdapter {
        return AddedExercisesAdapter(addedExercises) { exerciseToDelete ->
            removeExercise(exerciseToDelete, addedExercises)
        }
    }

    private fun isDateInPast(dateString: String): Boolean {
        val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
        val currentDate = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time

        return selectedDate.before(currentDate)
    }






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = intent.getIntExtra("userId", -1)
        val exercises = SharedPrefUtil.loadExercises(this)
        val addedExercises = mutableListOf<Exercise>()
        selectedDate = intent.getStringExtra("SELECTED_DATE") ?: getCurrentDate()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, exercises)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.exerciseSpinner.adapter = adapter

        binding.submitNewExerciseButton.setOnClickListener {
            val newExerciseName = binding.newExerciseEditText.text.toString().trim()
            if (newExerciseName.isNotEmpty()) {
                exercises.add(newExerciseName)
                adapter.notifyDataSetChanged()
                binding.newExerciseEditText.text.clear()
                Toast.makeText(this, "New exercise added.", Toast.LENGTH_SHORT).show()
                SharedPrefUtil.saveExercises(this, exercises)
            } else {
                Toast.makeText(this, "Please enter a valid exercise name.", Toast.LENGTH_SHORT).show()
            }
        }

        val addedExercisesAdapter = createAddedExercisesAdapter(addedExercises)
        fun saveExercisesToDatabase(onFinished: () -> Unit) {
            val repository = FitnessRepository(this)
            CoroutineScope(Dispatchers.IO).launch {
                for (exercise in addedExercises) {
                    val exerciseToSave = Exercise(
                        userId = userId,
                        name = exercise.name,
                        reps = exercise.reps,
                        weight = exercise.weight,
                        date = selectedDate
                    )
                    repository.addExercise(exerciseToSave)
                }
                withContext(Dispatchers.Main) {
                    onFinished()
                }
            }
        }


        binding.addedExercisesRecyclerView.adapter = addedExercisesAdapter
        binding.addedExercisesRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.addExerciseButton.setOnClickListener {
            if (isDateInPast(selectedDate)) {
                Toast.makeText(this, "Adding exercise to a past date is not permitted.", Toast.LENGTH_SHORT).show()
            } else {
                val exerciseName = binding.exerciseSpinner.selectedItem.toString()
                val weightString = binding.weightEditText.text.toString()
                val repsString = binding.repsEditText.text.toString()

                if (weightString.isNotEmpty() && repsString.isNotEmpty()) {
                    val weight = weightString.toInt()
                    val reps = repsString.toInt()
                    val exercise = Exercise(name = exerciseName, weight = weight, reps = reps)
                    addedExercises.add(exercise)
                    saveExercisesToDatabase {
                        val intent = Intent(this@AddWorkoutActivity, DashboardActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        intent.putExtra("SELECTED_DATE", selectedDate)
                        startActivity(intent)
                        finish()
                    }
                    binding.weightEditText.text.clear()
                    binding.repsEditText.text.clear()
                } else {
                    Toast.makeText(this, "Please enter valid weight and reps.", Toast.LENGTH_SHORT).show()
                }
            }
        }


        fun getCurrentDate(): String {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(calendar.time)
        }




        binding.backToDashboardButton.setOnClickListener {
            saveExercisesToDatabase {
                val intent = Intent(this@AddWorkoutActivity, DashboardActivity::class.java)
                intent.putExtra("USER_ID", userId)
                intent.putExtra("SELECTED_DATE", selectedDate)
                startActivity(intent)
                finish()
            }
        }


    }



    }

