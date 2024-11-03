package com.kookie

import android.content.Context
import com.kookie.internal.utils.DownloadLogger
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class Kookie private constructor(
    private val context: Context,
    private var downloadConfig: DownloadTimeout,
    private var notificationConfig: NotificationConfig,
    private var logger: Logger,
    private var okHttpClient: OkHttpClient
) {

    class Builder {
        private var _notificationConfig: NotificationConfig = NotificationConfig.Builder().build()
        private var _downloadTimeout: DownloadTimeout = DownloadTimeout.Builder().build()
        private lateinit var _logger: Logger
        private lateinit var _okHttpClient: OkHttpClient
        fun configureNotification(notificationConfig: NotificationConfig.Builder.() -> Unit) {
            _notificationConfig = NotificationConfig.Builder().apply(notificationConfig).build()
        }

        fun configureDownloadTimeout(downloadTimeout: DownloadTimeout.Builder.() -> Unit) {
            _downloadTimeout = DownloadTimeout.Builder().apply(downloadTimeout).build()
        }

        var logger: Logger
            get() = _logger
            set(value) {
                _logger = value
            }

        var okHttpClient: OkHttpClient
            get() = _okHttpClient
            set(value) {
                _okHttpClient = value
            }

        fun build(context: Context): Kookie {
            if (!::_okHttpClient.isInitialized) {
                okHttpClient = OkHttpClient
                    .Builder()
                    .connectTimeout(_downloadTimeout.connectTimeOutInMs, TimeUnit.MILLISECONDS)
                    .readTimeout(_downloadTimeout.readTimeOutInMs, TimeUnit.MILLISECONDS)
                    .build()
            }
            if (!::_logger.isInitialized) {
                _logger = DownloadLogger(false)
            }
            return Kookie(
                context = context.applicationContext,
                downloadConfig = _downloadTimeout,
                notificationConfig = _notificationConfig,
                logger = _logger,
                okHttpClient = _okHttpClient
            )
        }
    }

    companion object {
        fun create(context: Context, builder: Builder.() -> Unit): Kookie {
            return Builder().apply(builder).build(context)
        }
    }

}

