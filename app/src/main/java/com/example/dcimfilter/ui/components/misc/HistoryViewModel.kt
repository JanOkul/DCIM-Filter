package com.example.dcimfilter.ui.components.misc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.dcimfilter.room.history.HistoryDao

class HistoryViewModel(private val historyDao: HistoryDao) : ViewModel() {

    val historyFlow = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { historyDao.getHistoryPaged() }
    ).flow.cachedIn(viewModelScope)
}