package com.example.dcimfilter.queue

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 *  An implementation of an ID ordered queue using Android Room databases.
 */
@Database(
    entities = [FilterTarget::class],
    version = 1
)
abstract class FilterDB: RoomDatabase() {
    abstract val filterDao: FilterDao

    companion object {
        @Volatile private var INSTANCE: FilterDB? = null

        fun getInstance(context: Context) = INSTANCE ?: Room.databaseBuilder(
            context.applicationContext,
            FilterDB::class.java,
            "filterQueue.db"
        ).build().also { INSTANCE = it }
    }
}