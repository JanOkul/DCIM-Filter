package com.janokul.dcimfilter.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janokul.dcimfilter.room.rule.FilterRule
import com.janokul.dcimfilter.room.rule.FilterRuleDao
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainViewModel @Inject constructor (
    val filterRuleDao: FilterRuleDao
): ViewModel() {

    val rules = filterRuleDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     *  Creates a new entry in Room, so user can navigate to the appropriate page, and actually
     *  create a new entry, which will update this empty entry
     */
    suspend fun newRuleBlank(): Long {
        return filterRuleDao.insert(
            FilterRule(
                fromRelativePath = "",
                toRelativePath = "",
                rules = emptyList()
            )
        )
    }
}