package com.example.dcimfilter.room.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uriId: Long,
    val mimeType: String,
    val movedAt: Long = System.currentTimeMillis(),
    val filename: String,
    val movedTo: String
)