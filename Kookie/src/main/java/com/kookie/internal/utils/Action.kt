package com.kookie.internal.utils

internal sealed interface Action {

    data object Pause : Action

    data object Resume : Action

    data object Cancel : Action

    data object Retry : Action

    data object Start : Action

    data object Default : Action

}