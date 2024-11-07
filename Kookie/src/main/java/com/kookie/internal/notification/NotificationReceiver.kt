package com.ketch.internal.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kookie.Kookie
import com.kookie.internal.utils.*
import com.kookie.internal.utils.DownloadUtil.getTotalLengthText

internal class NotificationReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return
        intent.action ?: return
        val kookie = Kookie.getInstance() ?: return
        when (intent.action) {
            NOTIFICATION_RESUME_ACTION -> handleAction(
                context, intent, kookie::resume
            )

            NOTIFICATION_RETRY_ACTION -> handleAction(
                context, intent, kookie::retry
            )

            NOTIFICATION_PAUSE_ACTION -> handleAction(
                context, intent, kookie::pause
            )

            NOTIFICATION_CANCEL_ACTION -> handleAction(
                context, intent, kookie::cancel
            )

            else -> handleNotificationAction(context, intent)
        }
    }

    private fun handleAction(context: Context, intent: Intent, action: (Int) -> Unit) {
        val requestId = intent.extras?.getInt(REQUEST_ID_KEY)
        val notificationId = intent.extras?.getInt(NOTIFICATION_ID_KEY)
        notificationId?.let { NotificationManagerCompat.from(context).cancel(it) }
        requestId?.let(action)
    }

    @SuppressLint("MissingPermission")
    private fun handleNotificationAction(context: Context, intent: Intent) {
        val notificationId = intent.extras?.getInt(REQUEST_ID_KEY)?.plus(1) ?: return
        val notificationChannelName = intent.getStringExtraOrDefault(
            NOTIFICATION_CHANNEL_NAME_KEY, DEFAULT_NOTIFICATION_CHANNEL_NAME
        )
        val notificationImportance = intent.getIntExtraOrDefault(
            NOTIFICATION_CHANNEL_IMPORTANCE_KEY, DEFAULT_NOTIFICATION_CHANNEL_IMPORTANCE
        )
        val notificationChannelDescription = intent.getStringExtraOrDefault(
            NOTIFICATION_CHANNEL_DESCRIPTION_KEY, DEFAULT_NOTIFICATION_CHANNEL_DESCRIPTION
        )
        val notificationSmallIcon = intent.getIntExtraOrDefault(
            SMALL_NOTIFICATION_ICON_KEY, DEFAULT_SMALL_NOTIFICATION_ICON
        )
        val fileName = intent.extras?.getString(FILE_NAME_KEY) ?: ""
        val currentProgress = intent.getIntExtraOrDefault(PROGRESS_KEY, 0)
        val totalLength = intent.getLongExtraOrDefault(LENGTH_KEY, DEFAULT_LENGTH_VALUE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                context,
                notificationChannelName,
                notificationImportance,
                notificationChannelDescription
            )
        }

        val notificationBuilder =
            NotificationCompat.Builder(context, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(notificationSmallIcon)
                .setContentText(getNotificationText(intent.action, totalLength, currentProgress))
                .setContentTitle(fileName)
                .setContentIntent(
                    createPendingIntent(
                        context,
                        notificationId,
                        REQUEST_ID_KEY,
                        NOTIFICATION_DISMISSED_ACTION
                    )
                )
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)

        addActionButtons(
            context,
            intent.action,
            notificationId,
            notificationBuilder,
            currentProgress
        )

        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
    }

    private fun getNotificationText(action: String?, totalLength: Long, currentProgress: Int) =
        when (action) {
            DOWNLOAD_COMPLETED_ACTION -> "Download successful. (${getTotalLengthText(totalLength)})"
            DOWNLOAD_FAILED_ACTION -> "Download failed."
            DOWNLOAD_PAUSED_ACTION -> "Download paused."
            else -> "Download cancelled."
        }.plus(if (action == DOWNLOAD_PAUSED_ACTION || action == DOWNLOAD_FAILED_ACTION) " $currentProgress%" else "")

    private fun addActionButtons(
        context: Context,
        action: String?,
        notificationId: Int,
        builder: NotificationCompat.Builder,
        currentProgress: Int
    ) {
        when (action) {
            DOWNLOAD_FAILED_ACTION -> {
                builder.addAction(
                    -1,
                    RETRY_BUTTON_LABEL,
                    createPendingIntent(
                        context,
                        notificationId,
                        REQUEST_ID_KEY,
                        NOTIFICATION_RETRY_ACTION
                    )
                )
                    .addAction(
                        -1,
                        CANCEL_BUTTON_LABEL,
                        createPendingIntent(
                            context,
                            notificationId,
                            REQUEST_ID_KEY,
                            NOTIFICATION_CANCEL_ACTION
                        )
                    )
                    .setProgress(MAX_PROGRESS_VALUE, currentProgress, false)
            }

            DOWNLOAD_PAUSED_ACTION -> {
                builder.addAction(
                    -1,
                    RESUME_BUTTON_LABEL,
                    createPendingIntent(
                        context,
                        notificationId,
                        REQUEST_ID_KEY,
                        NOTIFICATION_RESUME_ACTION
                    )
                )
                    .addAction(
                        -1,
                        CANCEL_BUTTON_LABEL,
                        createPendingIntent(
                            context,
                            notificationId,
                            REQUEST_ID_KEY,
                            NOTIFICATION_CANCEL_ACTION
                        )
                    )
                    .setProgress(MAX_PROGRESS_VALUE, currentProgress, false)
            }
        }
    }

    private fun createPendingIntent(
        context: Context,
        notificationId: Int,
        requestKey: String,
        action: String
    ): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            this.action = action
            putExtra(NOTIFICATION_ID_KEY, notificationId)
            putExtra(requestKey, notificationId - 1) // Adjust back to requestId
        }
        return PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        context: Context,
        name: String,
        importance: Int,
        description: String
    ) {
        val channel =
            NotificationChannel(DOWNLOAD_NOTIFICATION_CHANNEL_ID, name, importance).apply {
                this.description = description
            }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun Intent.getStringExtraOrDefault(key: String, defaultValue: String) =
        extras?.getString(key) ?: defaultValue

    private fun Intent.getIntExtraOrDefault(key: String, defaultValue: Int) =
        extras?.getInt(key) ?: defaultValue

    private fun Intent.getLongExtraOrDefault(key: String, defaultValue: Long) =
        extras?.getLong(key) ?: defaultValue
}
