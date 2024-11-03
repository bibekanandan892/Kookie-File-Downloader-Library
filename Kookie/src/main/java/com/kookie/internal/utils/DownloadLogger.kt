package com.kookie.internal.utils

import android.util.Log
import com.kookie.LogType
import com.kookie.Logger

internal class DownloadLogger(private val enableLogs: Boolean) : Logger {
    override fun log(tag: String?, msg: String?, tr: Throwable?, type: LogType) {
        if (enableLogs) {
            when (type) {
                LogType.Verbose -> Log.v(tag, msg, tr)
                LogType.Debug -> Log.d(tag, msg, tr)
                LogType.Info -> Log.i(tag, msg, tr)
                LogType.Warn -> Log.w(tag, msg, tr)
                LogType.Error -> Log.e(tag, msg, tr)
            }
        }
    }
}
