package com.janokul.dcimfilter.room.queue

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 *  A single entry of a file that needs to be relocated by the filtering service.
 *  @param id Used to order which file should be relocated first.
 *  @param name The filename of the media to be moved.
 *  @param uriId The MediaStore ID for the target media.
 *  @param mimeType The type of the media (image, video, audio).
 *  @param destinationFolder The subfolder to move the media to.
 */
@Entity(indices = [Index(value = ["uriId"], unique = true)])
data class FilterTarget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val uriId: Long,
    val mimeType: String,
    val destinationFolder: String // Name of subfolder
)
