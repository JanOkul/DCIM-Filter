package com.janokul.dcimfilter.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.janokul.dcimfilter.room.history.HistoryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor (
    private val historyDao: HistoryDao
) : ViewModel() {

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
}