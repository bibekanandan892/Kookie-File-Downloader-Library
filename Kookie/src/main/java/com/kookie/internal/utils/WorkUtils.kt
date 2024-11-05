package com.kookie.internal.utils

import android.content.Context
import androidx.core.app.NotificationManagerCompat

fun removeNotification(context: Context, notificationId: Int) {
    NotificationManagerCompat.from(context).cancel(notificationId)
}