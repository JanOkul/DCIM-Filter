package com.janokul.dcimfilter.ui.components.misc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.janokul.dcimfilter.room.history.HistoryDao

class HistoryViewModel(private val historyDao: HistoryDao) : ViewModel() {

    val historyPaged = Pager(
        config = PagingConfig(
            pageSize = 30,
            prefetchDistance = 5,
            enablePlaceholders = false,
            maxSize = 50
        )
    ) {
        historyDao.getHistoryPaged()
    }.flow.cachedIn(viewModelScope)

    companion object {
        fun factory(dao: HistoryDao): ViewModelProvider.Factory = viewModelFactory {
            initializer { HistoryViewModel(dao) }
        }
    }
}