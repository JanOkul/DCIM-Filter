package com.example.dcimfilter.room.history

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {

    @Insert
    suspend fun insertHistoryEntry(entry: History)

    @Query("SELECT * FROM History ORDER BY movedAt DESC")
    fun getHistoryPaged(): PagingSource<Int, History>

    @Query("SELECT * FROM History ORDER BY movedAt DESC")
    fun getHistory(): List<History>

    @Query("SELECT COUNT(*) FROM History")
    fun getCount(): Int
}