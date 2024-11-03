package com.kookie

import kotlinx.serialization.Serializable

@Serializable
class NotificationConfig private constructor(
    val enabled: Boolean = NotificationConst.DEFAULT_VALUE_NOTIFICATION_ENABLED,
    val channelName: String = NotificationConst.DEFAULT_VALUE_NOTIFICATION_CHANNEL_NAME,
    val channelDescription: String = NotificationConst.DEFAULT_VALUE_NOTIFICATION_CHANNEL_DESCRIPTION,
    val importance: Int = NotificationConst.DEFAULT_VALUE_NOTIFICATION_CHANNEL_IMPORTANCE,
    val showSpeed: Boolean = true,
    val showSize: Boolean = true,
    val showTime: Boolean = true,
    val smallIcon: Int
) {
    class Builder {
        private var _enabled: Boolean = NotificationConst.DEFAULT_VALUE_NOTIFICATION_ENABLED
        private var _channelName: String = NotificationConst.DEFAULT_VALUE_NOTIFICATION_CHANNEL_NAME
        private var _channelDescription: String =
            NotificationConst.DEFAULT_VALUE_NOTIFICATION_CHANNEL_DESCRIPTION
        private var _importance: Int =
            NotificationConst.DEFAULT_VALUE_NOTIFICATION_CHANNEL_IMPORTANCE
        private var _showSpeed: Boolean = true
        private var _showSize: Boolean = true
        private var _showTime: Boolean = true
        private var _smallIcon: Int = R.drawable.ic_launcher_background

        var enabled: Boolean
            get() = _enabled
            set(value) {
                _enabled = value
            }

        var channelName: String
            get() = _channelName
            set(value) {
                _channelName = value
            }

        var channelDescription: String
            get() = _channelDescription
            set(value) {
                _channelDescription = value
            }

        var importance: Int
            get() = _importance
            set(value) {
                _importance = value
            }
        var showSpeed: Boolean
            get() = _showSpeed
            set(value) {
                _showSpeed = value
            }
        var showSize: Boolean
            get() = _showSize
            set(value) {
                _showSize = showSize
            }
        var showTime: Boolean
            get() = _showTime
            set(value) {
                _showTime = value
            }
        var smallIcon: Int
            get() = _smallIcon
            set(value) {
                _smallIcon = value
            }

        fun build(): NotificationConfig {
            return NotificationConfig(
                enabled = _enabled,
                channelName = _channelName,
                channelDescription = _channelDescription,
                importance = _importance,
                showSpeed = _showSpeed,
                showTime = _showTime,
                smallIcon = _smallIcon
            )
        }
    }
}
