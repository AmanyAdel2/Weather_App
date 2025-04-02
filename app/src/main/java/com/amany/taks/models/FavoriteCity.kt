package com.amany.taks.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "favorite_table")
data class FavoriteCity(
    @PrimaryKey val name: String,
    val lat: Double,
    val lon: Double,
    val countryCode: String?
) : Serializable
