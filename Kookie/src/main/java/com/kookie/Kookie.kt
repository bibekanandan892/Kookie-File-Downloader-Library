package com.kookie

import android.annotation.SuppressLint
import android.content.Context
import com.kookie.internal.network.Retrofit
import com.kookie.internal.utils.DownloadLogger
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class Kookie private constructor(
    private val context: Context,
    private val downloadConfig: DownloadTimeout,
    private val notificationConfig: NotificationConfig,
    private val logger: Logger,
    private val okHttpClient: OkHttpClient
) {
    init {
        Retrofit.getDownloadService(okHttpClient = okHttpClient)
    }

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
        @SuppressLint("StaticFieldLeak")
        /**
         * The ApplicationContext is tied to the lifecycle of the entire app, not individual activities, so this avoids memory leaks.
         */
        @Volatile
        private var instance: Kookie? = null

        fun create(context: Context, builder: Builder.() -> Unit): Kookie {
            return instance ?: synchronized(this) {
                instance ?: Builder().apply(builder).build(context.applicationContext).also {
                    instance = it
                }
            }
        }
        internal fun getInstance(): Kookie? = instance
    }

}
