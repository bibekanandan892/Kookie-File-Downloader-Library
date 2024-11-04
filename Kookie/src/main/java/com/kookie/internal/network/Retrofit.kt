package com.kookie.internal.network

import com.kookie.internal.utils.Constant.BASE_URL
import com.kookie.internal.utils.Constant.DEFAULT_VALUE_CONNECT_TIMEOUT_MS
import com.kookie.internal.utils.Constant.DEFAULT_VALUE_READ_TIMEOUT_MS
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

internal object Retrofit {

    @Volatile
    private var downloadService: DownloadService? = null

    fun getDownloadService(
        okHttpClient: OkHttpClient =
            OkHttpClient
                .Builder()
                .connectTimeout(DEFAULT_VALUE_CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_VALUE_READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .build()
    ): DownloadService {
        if (downloadService == null) {
            synchronized(this) {
                if (downloadService == null) {
                    downloadService = Retrofit
                        .Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .build()
                        .create(DownloadService::class.java)
                }
            }
        }
        return downloadService!!
    }
}
