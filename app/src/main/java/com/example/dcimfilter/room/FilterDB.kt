package com.example.dcimfilter.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dcimfilter.room.history.History
import com.example.dcimfilter.room.history.HistoryDao
import com.example.dcimfilter.room.queue.FilterDao
import com.example.dcimfilter.room.queue.FilterTarget

/**
 *  An implementation of an ID ordered queue using Android Room databases.
 */
@Database(
    entities = [FilterTarget::class, History::class],
    version = 1
)
abstract class FilterDB : RoomDatabase() {
    abstract val filterDao: FilterDao
    abstract val historyDao: HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: FilterDB? = null

        fun getInstance(context: Context) = INSTANCE ?: Room.databaseBuilder(
            context.applicationContext,
            FilterDB::class.java,
            "filterQueue.db"
        ).build().also { INSTANCE = it }
    }
}