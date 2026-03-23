package com.example.dcimfilter.room.queue

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// todo update this
/**
 *  A single entry of a file that needs to be relocated by the filtering service.
 *  @param id Used to order which file should be relocated first.
 *  @param fileLocation The current location of the file.
 *  @param to Where the file is going to be moved to.
 */
@Entity(indices = [Index(value = ["uriId"], unique = true)])
data class FilterTarget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val uriId: Long,
    val mimeType: String,
    // User settings
    val destinationFolder: String // Name of subfolder
)
