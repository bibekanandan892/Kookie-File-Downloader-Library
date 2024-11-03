package com.kookie

data class DownloadFile(
    val downloadUrl: String,
    val savePath: String,
    val name: String,
    val downloadTag: String,
    val downloadId: Int,
    val requestHeaders: HashMap<String, String>,
    val queuedAt: Long,
    val downloadStatus: Status,
    val fileSize: Long,
    val downloadProgress: Int,
    val downloadSpeed: Float,
    val lastUpdate: Long,
    val entityTag: String,
    val additionalInfo: String,
    val errorReason: String
)
