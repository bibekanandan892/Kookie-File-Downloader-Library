package com.kookie.internal.download

import com.kookie.internal.utils.getUniqueId
import kotlinx.serialization.Serializable

@Serializable
internal data class FileDownloadRequest(
    val downloadUrl: String,
    val destinationPath: String,
    val outputFileName: String,
    val requestTag: String,
    val requestId: Int = getUniqueId(downloadUrl, destinationPath, outputFileName),
    val requestHeaders: Map<String, String> = emptyMap(),
    val additionalInfo: String = ""
)