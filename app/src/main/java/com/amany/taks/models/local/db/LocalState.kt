package com.amany.taks.models.local.db

import com.amany.taks.models.FavoriteCity

sealed class LocalState() {
    data class Success(val favoriteCity: List<FavoriteCity>) : LocalState()
    data class Failure(val msg: Throwable) : LocalState()
    object Loading : LocalState()

}