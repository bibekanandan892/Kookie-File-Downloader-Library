package com.kookie.internal.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kookie.Status
import com.kookie.internal.utils.Action
import kotlinx.serialization.Serializable


@Serializable
@Entity(tableName = "downloads")
data class DownloadEntity(
    var downloadUrl: String = "",
    var destinationPath: String = "",
    var fileName: String = "",
    var label: String = "",
    @PrimaryKey
    var id: Int = 0,
    var headerJson: String = "",
    var queueTime: Long = 0,
    var currentStatus: String = Status.Default.toString(),
    var totalSize: Long = 0,
    var downloadedSize: Long = 0,
    var speedPerMs: Float = 0f,
    var uniqueId: String = "",
    var modifiedTime: Long = 0,
    var eTag: String = "",
    var action: String = Action.Default.toString(),
    var metadata: String = "",
    var errorReason: String = ""
)
