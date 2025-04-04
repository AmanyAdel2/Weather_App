package com.amany.taks.models.local.db

sealed class LocalState {
    object Loading : LocalState()
    data class Success<T>(val data: T) : LocalState()
    data class Failure(val msg: Throwable) : LocalState()
}