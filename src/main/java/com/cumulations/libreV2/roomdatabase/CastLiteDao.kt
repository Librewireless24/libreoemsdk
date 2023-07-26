package com.cumulations.libreV2.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
/**
 * Created By SHAIK
 */
@Dao
interface CastLiteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDeviceUUID(castLiteData: CastLiteUUIDDataClass): Long

    @Query("SELECT * FROM uuidDataClass ORDER BY device_ip DESC")
    fun getAllDeviceUUID(): List<CastLiteUUIDDataClass>

    @Update
    fun updateDeviceUUID(castLiteData: CastLiteUUIDDataClass)

    @Delete
    fun deleteAllDeviceUUID(castLiteData: CastLiteUUIDDataClass)

    @Query("DELETE FROM uuidDataClass WHERE device_ip = :device_ip")
    fun deleteDeviceUUID(device_ip: String)

    @Query("SELECT * FROM uuidDataClass WHERE device_ip LIKE :device_ip")
    fun getDeviceUUID(device_ip: String):CastLiteUUIDDataClass
}