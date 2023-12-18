package com.cumulations.libreV2.roomdatabase

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created By SHAIK
 * 15/JUNE/2023
 */
@Entity(tableName = "passwordRememberDataClass")
data class PasswordRememberDataClass(
    @PrimaryKey(autoGenerate = true)
    val Id :Int,
    @NonNull
    @ColumnInfo(name = "device_ssid")
    var deviceSSID: String,
    @ColumnInfo(name = "password")
    var password: String
)