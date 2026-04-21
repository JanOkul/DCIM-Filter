package com.janokul.dcimfilter.room

import android.content.Context
import androidx.room.Room
import com.janokul.dcimfilter.DB_NAME
import com.janokul.dcimfilter.room.history.HistoryDao
import com.janokul.dcimfilter.room.rule.FilterRuleDao
import com.janokul.dcimfilter.room.target.FilterTargetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DcimFilterDb {
        return Room.databaseBuilder(
            context,
            DcimFilterDb::class.java,
            DB_NAME
        ).build()
    }

    @Provides
    fun provideFilterTargetDao(database: DcimFilterDb): FilterTargetDao {
        return database.filterTargetDao
    }

    @Provides
    fun provideHistoryDao(database: DcimFilterDb): HistoryDao {
        return database.historyDao
    }

    @Provides
    fun provideFilterRuleDao(database: DcimFilterDb): FilterRuleDao {
        return database.filterRuleDao
    }
}