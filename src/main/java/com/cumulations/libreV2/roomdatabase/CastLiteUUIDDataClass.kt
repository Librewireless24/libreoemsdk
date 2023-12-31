package com.cumulations.libreV2.roomdatabase

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Created By SHAIK
 */
@Entity(tableName = "uuidDataClass")
data class CastLiteUUIDDataClass(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name ="id") var id: Int,
    @NonNull
    @ColumnInfo(name ="device_ip") var deviceIP: String ,
    @ColumnInfo(name ="request_type") var requestType: String,
    @ColumnInfo(name ="device_uuid") var deviceUuid: String
)
