package com.janokul.dcimfilter.ui.rule

import androidx.lifecycle.ViewModel
import com.janokul.dcimfilter.room.rule.FilterRuleDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RuleViewModel @Inject constructor(
    private val dao: FilterRuleDao
): ViewModel() {


    val filterRule = dao.getAll()
}