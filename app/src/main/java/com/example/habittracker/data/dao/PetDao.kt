package com.example.habittracker.data.dao

import androidx.room.*
import com.example.habittracker.data.model.PetState
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pet_state WHERE id = 1")
    fun getPetState(): Flow<PetState?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePetState(petState: PetState)
}
