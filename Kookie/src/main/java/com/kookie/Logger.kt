package com.kookie
interface Logger {
    companion object {
        const val TAG = "Kookie Log"
    }

    fun log(
        tag: String? = TAG,
        message: String? = "",
        throwable: Throwable? = null,
        type: LogType = LogType.Debug
    )
}
sealed interface LogType {
    data object Verbose : LogType
    data object Debug : LogType
    data object Info : LogType
    data object Warn : LogType
    data object Error : LogType
}