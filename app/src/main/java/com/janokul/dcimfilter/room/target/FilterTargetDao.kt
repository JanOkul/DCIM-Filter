package com.janokul.dcimfilter.room.target

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

/**
 *  The interface for the Room database.
 */
@Dao
interface FilterTargetDao {

    /**
     *  Used to insert a new entry into the queue.
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFilterTarget(filterTarget: FilterTarget)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FilterTarget>)

    @Transaction
    suspend fun claimNext(): FilterTarget? {
        val entry = peekFilterTarget() ?: return null
        deleteFilterTarget(entry)
        return entry
    }

    @Transaction
    @Query("SELECT * FROM FilterTarget WHERE uriId = :uriId")
    suspend fun getByUriId(uriId: Long): FilterTarget

    @Transaction
    suspend fun claimByUriId(uriId: Long): FilterTarget? {
        val entry = getByUriId(uriId)
        deleteFilterTarget(entry)
        return entry
    }

    /**
     *  Used to get the first entry in the queue.
     */
    @Transaction
    @Query("SELECT * FROM FilterTarget ORDER BY id ASC LIMIT 1")
    suspend fun peekFilterTarget(): FilterTarget?

    /**
     *  Used to delete an entry in the queue (must use the peek method to get the entry first).
     */
    @Transaction
    @Delete
    suspend fun deleteFilterTarget(filterTarget: FilterTarget)

    @Transaction
    @Query("SELECT COUNT(*) FROM FilterTarget")
    suspend fun getCount(): Int
}