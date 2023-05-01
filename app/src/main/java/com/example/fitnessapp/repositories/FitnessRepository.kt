// Repositories/FitnessRepository.kt
package com.example.fitnessapp.repositories

import com.example.fitnessapp.models.Exercise
import com.example.fitnessapp.models.FoodEntry
import com.example.fitnessapp.models.User
import android.content.Context
import androidx.lifecycle.LiveData
import com.example.fitnessapp.data.FitnessDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt

class FitnessRepository(context: Context) {
    private val fitnessDao = FitnessDatabase.getInstance(context).fitnessDao()

    suspend fun createUser(username: String, password: String): Long {
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
        return withContext(Dispatchers.IO) {
            fitnessDao.insertUser(User(username = username.toLowerCase(), passwordHash = passwordHash))
        }
    }

    suspend fun authenticateUser(username: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            val user = fitnessDao.getUserByUsername(username.toLowerCase())
            if (user != null && BCrypt.checkpw(password, user.passwordHash)) {
                user
            } else {
                null
            }
        }
    }




    // FoodEntry-related functions
    fun addFoodEntry(foodEntry: FoodEntry): Long {
        return fitnessDao.insertFoodEntry(foodEntry)
    }

    fun getFoodEntriesByDate(userId: Int, date: String): List<FoodEntry> {
        return fitnessDao.getFoodEntriesByDate(userId, date)
    }

    // Exercise-related functions
    fun addExercise(exercise: Exercise): Long {
        return fitnessDao.insertExercise(exercise)
    }

    fun getExercisesByDate(userId: Int, date: String): List<Exercise> {
        return fitnessDao.getExercisesByDate(userId, date)
    }

    fun getAllExercises(userId: Int): List<Exercise> {
        return fitnessDao.getAllExercises(userId)
    }

    suspend fun deleteExercise(id: Int) {
        withContext(Dispatchers.IO) {
            fitnessDao.deleteExercise(id)
        }
    }


    fun updateExercise(id: Int, name: String, reps: Int, weight: Int) {
        fitnessDao.updateExercise(id, name, reps, weight)
    }

    suspend fun deleteFoodEntry(id: Int) {
        withContext(Dispatchers.IO) {
            fitnessDao.deleteFoodEntry(id)
        }
    }

    fun getUserById(userId: Int): LiveData<User?> {
        return fitnessDao.getUserById(userId)
    }



    suspend fun getDailyNutritionData(userId: Int, date: String): NutritionData {
        return withContext(Dispatchers.IO) {
            val foodEntries = fitnessDao.getFoodEntriesByDate(userId, date)
            var totalCalories = 0
            var totalProtein = 0
            var totalCarbohydrates = 0
            var totalFat = 0

            for (foodEntry in foodEntries) {
                totalCalories += foodEntry.calories
                totalProtein += foodEntry.protein
                totalCarbohydrates += foodEntry.carbs
                totalFat += foodEntry.fat
            }

            NutritionData(totalCalories, totalProtein, totalCarbohydrates, totalFat)
        }
    }


    data class NutritionData(
        val totalCalories: Int,
        val totalProtein: Int,
        val totalCarbohydrates: Int,
        val totalFat: Int
    )


}
