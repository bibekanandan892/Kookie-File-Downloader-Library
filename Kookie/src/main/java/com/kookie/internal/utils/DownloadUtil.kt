package com.kookie.internal.utils

internal object DownloadUtil {

    private const val MAX_PERCENT = 100
    private const val MS_IN_SEC = 1000
    private const val KB = 1024
    private const val KB_THRESHOLD = 500

    // Time calculation utility
    internal class TimeUtil {

        companion object {
            private const val SECONDS_IN_MINUTE = 60
            private const val MINUTES_IN_HOUR = 60

            fun calculateRemainingTime(
                speedInBytesPerMs: Float,
                progressPercent: Int,
                totalBytes: Long
            ): String {
                if (speedInBytesPerMs == 0F) return ""

                val speedInBytesPerSec = speedInBytesPerMs * MS_IN_SEC
                val bytesRemaining =
                    (totalBytes * (MAX_PERCENT - progressPercent) / MAX_PERCENT).toFloat()
                val timeLeftInSec = bytesRemaining / speedInBytesPerSec
                val timeLeftInMin = timeLeftInSec / SECONDS_IN_MINUTE
                val timeLeftInHour = timeLeftInMin / SECONDS_IN_MINUTE

                return when {
                    timeLeftInSec < SECONDS_IN_MINUTE -> "%.0f s left".format(timeLeftInSec)
                    timeLeftInMin < SECONDS_IN_MINUTE -> "%.0f mins %.0f s left".format(
                        timeLeftInMin,
                        timeLeftInSec % SECONDS_IN_MINUTE
                    )

                    timeLeftInMin < KB_THRESHOLD -> "%.0f mins left".format(timeLeftInMin)
                    timeLeftInHour < KB_THRESHOLD -> "%.0f hrs %.0f mins left".format(
                        timeLeftInHour,
                        timeLeftInMin % MINUTES_IN_HOUR
                    )

                    else -> "%.0f hrs left".format(timeLeftInHour)
                }
            }
        }
    }

    // Speed calculation utility
    internal class SpeedUtil {

        companion object {
            fun formatSpeed(speedInBytesPerMs: Float): String {
                var value = speedInBytesPerMs * MS_IN_SEC
                val units = arrayOf("b/s", "kb/s", "mb/s", "gb/s")
                var unitIndex = 0

                while (value >= KB_THRESHOLD && unitIndex < units.size - 1) {
                    value /= KB
                    unitIndex++
                }

                return "%.2f %s".format(value, units[unitIndex])
            }
        }
    }

    // Length calculation utility
    internal class LengthUtil {

        companion object {
            fun formatLength(totalBytes: Long): String {
                var value = totalBytes.toFloat()
                val units = arrayOf("b", "kb", "mb", "gb")
                var unitIndex = 0

                while (value >= KB_THRESHOLD && unitIndex < units.size - 1) {
                    value /= KB
                    unitIndex++
                }

                return "%.2f %s".format(value, units[unitIndex])
            }
        }
    }

    // High-level methods that utilize the helper classes
    fun getRemainingTimeText(
        speedInBytesPerMs: Float,
        progressPercent: Int,
        totalBytes: Long
    ): String {
        return TimeUtil.calculateRemainingTime(speedInBytesPerMs, progressPercent, totalBytes)
    }

    fun getSpeedText(speedInBytesPerMs: Float): String {
        return SpeedUtil.formatSpeed(speedInBytesPerMs)
    }

    fun getTotalLengthText(totalBytes: Long): String {
        return LengthUtil.formatLength(totalBytes)
    }
}