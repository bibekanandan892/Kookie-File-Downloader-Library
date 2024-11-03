package com.kookie.internal

import com.kookie.internal.utils.Constant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

internal class RetrofitInstance {

    @Volatile
    private var downloadService: DownloadService? = null

    fun getDownloadService(
        okHttpClient: OkHttpClient =
            OkHttpClient
                .Builder()
                .connectTimeout(Constant.CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .readTimeout(Constant.READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .build()
    ): DownloadService {
        if (downloadService == null) {
            synchronized(this) {
                if (downloadService == null) {
                    downloadService = Retrofit
                        .Builder()
                        .baseUrl(Constant.BASE_URL)
                        .client(okHttpClient)
                        .build()
                        .create(DownloadService::class.java)
                }
            }
        }
        return downloadService!!
    }
}