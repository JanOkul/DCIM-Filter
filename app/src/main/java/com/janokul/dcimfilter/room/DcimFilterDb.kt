package com.janokul.dcimfilter.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.janokul.dcimfilter.DB_NAME
import com.janokul.dcimfilter.room.history.History
import com.janokul.dcimfilter.room.history.HistoryDao
import com.janokul.dcimfilter.room.rule.FilterRule
import com.janokul.dcimfilter.room.rule.FilterRuleDao
import com.janokul.dcimfilter.room.rule.ConditionListConverter
import com.janokul.dcimfilter.room.target.FilterTarget
import com.janokul.dcimfilter.room.target.FilterTargetDao

/**
 *  An implementation of an ID ordered queue using Android Room databases.
 */
@Database(
    entities = [FilterTarget::class, FilterRule::class, History::class],
    version = 1
)
@TypeConverters(ConditionListConverter::class)
abstract class DcimFilterDb : RoomDatabase() {
    abstract val filterTargetDao: FilterTargetDao
    abstract val historyDao: HistoryDao
    abstract val filterRuleDao: FilterRuleDao

    companion object {
        @Volatile
        private var INSTANCE: DcimFilterDb? = null

        fun getInstance(context: Context) = INSTANCE ?: Room.databaseBuilder(
            context.applicationContext,
            DcimFilterDb::class.java,
            DB_NAME
        ).build().also { INSTANCE = it }
    }
}