package com.kookie.internal.download

import com.kookie.internal.network.DownloadService
import com.kookie.internal.utils.HTTP_STATUS_RANGE_NOT_SATISFIABLE
import com.kookie.internal.utils.RANGE_HEADER
import com.kookie.internal.utils.deleteFileIfExists
import com.kookie.internal.utils.getTemporaryFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

internal class DownloadTask(
    private val fileUrl: String,
    private val destinationPath: String,
    private val destinationFileName: String,
    private val downloadService: DownloadService
) {
    companion object {
        private const val SUCCESS_CODE_MIN = 200
        private const val SUCCESS_CODE_MAX = 299
        private const val PROGRESS_UPDATE_INTERVAL = 1500L
    }

    suspend fun startDownload(
        headers: MutableMap<String, String> = mutableMapOf(),
        onDownloadStart: suspend (speed: Long) -> Unit,
        onDownloadProgress: suspend (downloadedBytes: Long, length: Long, speed: Float) -> Unit
    ): Long {
        var currentBytes = 0L
        val finalFile = File(destinationPath, destinationFileName)
        val tempFile = getTemporaryFile(finalFile)

        if (tempFile.exists()) {
            currentBytes = tempFile.length()
            headers[RANGE_HEADER] = "bytes=$currentBytes-"
        }

        var response = downloadService.downloadFromUrl(fileUrl, headers)

        // Retry without range if the response is unsatisfiable or redirected
        if (response.code() == HTTP_STATUS_RANGE_NOT_SATISFIABLE || isRedirected(
                response.raw().request().url().toString()
            )
        ) {
            deleteFileIfExists(destinationPath, destinationFileName)
            headers.remove(RANGE_HEADER)
            currentBytes = 0L
            response = downloadService.downloadFromUrl(fileUrl, headers)
        }

        val responseBody = response.body() ?: throw IOException("Failed: Response body is null")
        if (response.code() !in SUCCESS_CODE_MIN..SUCCESS_CODE_MAX) {
            throw IOException("Error: Response code ${response.code()}")
        }

        val totalBytes = responseBody.contentLength().takeIf { it >= 0 }
            ?: throw IOException("Invalid content length")

        responseBody.byteStream().use { input ->
            FileOutputStream(tempFile, true).use { output ->
                onDownloadStart(totalBytes + currentBytes)

                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var bytesRead: Int
                var progressBytes = 0L
                var lastUpdateTime = System.currentTimeMillis()

                while (input.read(buffer).also { bytesRead = it } >= 0) {
                    output.write(buffer, 0, bytesRead)
                    currentBytes += bytesRead
                    progressBytes += bytesRead

                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastUpdateTime >= PROGRESS_UPDATE_INTERVAL) {
                        val speed = progressBytes.toFloat() / (currentTime - lastUpdateTime)
                        progressBytes = 0L
                        lastUpdateTime = currentTime
                        onDownloadProgress(currentBytes, totalBytes, speed)
                    }
                }
                onDownloadProgress(totalBytes, totalBytes, 0f)
            }
        }

        check(tempFile.renameTo(finalFile)) { "Error: Could not rename temp file" }
        return totalBytes
    }

    private fun isRedirected(currentUrl: String) = currentUrl != fileUrl
}
