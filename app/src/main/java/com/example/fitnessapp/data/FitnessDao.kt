//FitnessDao.kt

package com.example.fitnessapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.fitnessapp.models.Exercise
import com.example.fitnessapp.models.FoodEntry
import com.example.fitnessapp.models.User

@Dao
interface FitnessDao {
    // User-related queries
    @Insert
    fun insertUser(user: User): Long

    @Query("SELECT * FROM User WHERE username = :username")
    fun getUserByUsername(username: String): User?

    @Query("DELETE FROM Exercise WHERE id = :id")
    fun deleteExercise(id: Int)

    @Transaction
    @Query("SELECT * FROM user WHERE id = :userId")
    fun getUserById(userId: Int): LiveData<User?>

    @Query("DELETE FROM FoodEntry WHERE id = :id")
    fun deleteFoodEntry(id: Int)

    // FoodEntry-related queries
    @Insert
    fun insertFoodEntry(foodEntry: FoodEntry): Long

    @Query("SELECT * FROM FoodEntry WHERE userId = :userId AND date = :date")
    fun getFoodEntriesByDate(userId: Int, date: String): List<FoodEntry>

    // Exercise-related queries
    @Insert
    fun insertExercise(exercise: Exercise): Long

    @Query("SELECT * FROM Exercise WHERE userId = :userId AND date = :date")
    fun getExercisesByDate(userId: Int, date: String): List<Exercise>

    @Query("SELECT * FROM Exercise WHERE userId = :userId")
    fun getAllExercises(userId: Int): List<Exercise>

    @Query("UPDATE Exercise SET name = :name, reps = :reps, weight = :weight WHERE id = :id")
    fun updateExercise(id: Int, name: String, reps: Int, weight: Int)
}
