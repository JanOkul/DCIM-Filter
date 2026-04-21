package com.janokul.dcimfilter.room.rule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterRuleDao {
    @Query("SELECT * FROM FilterRule")
    fun getAll(): Flow<List<FilterRule>>

    @Query("SELECT * FROM FilterRule WHERE id = :id")
    fun getById(id: Long?): Flow<FilterRule?>

    @Query("SELECT * FROM FilterRule WHERE fromRelativePath = :fromRelativePath")
    fun getByFromRelativePath(fromRelativePath: String): Flow<FilterRule?>

    @Insert
    suspend fun insert(rule: FilterRule): Long

    @Update
    suspend fun update(rule: FilterRule)

    @Delete
    suspend fun delete(rule: FilterRule)
}