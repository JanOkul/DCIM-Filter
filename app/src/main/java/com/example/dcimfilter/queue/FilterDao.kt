package com.example.dcimfilter.queue

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 *  The interface for the Room database.
 */
@Dao
interface FilterDao {

    /**
     *  Used to insert a new entry into the queue.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFilterTarget(filterTarget: FilterTarget)

    /**
     *  Used to get the first entry in the queue.
     */
    @Query("SELECT * FROM FilterTarget ORDER BY id ASC LIMIT 1")
    suspend fun peekFilterTarget(): FilterTarget

    /**
     *  Used to delete an entry in the queue (must use the peek method to get the entry first).
     */
    @Delete
    suspend fun deleteFilterTarget(filterTarget: FilterTarget)
}