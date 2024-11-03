package com.kookie

sealed interface Status {

    data object Queued : Status

    data object Started : Status

    data object InProgress : Status

    data object Success : Status

    data object Cancelled : Status

    data object Failed : Status

    data object Paused : Status

    data object Default : Status
}
