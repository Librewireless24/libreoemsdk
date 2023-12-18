package com.cumulations.libreV2.roomdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Created By SHAIK
 * 15/JUNE/2023
 */
@Dao
interface PasswordRememberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDeviceSSIDPWD(passwordRememberDataClass: PasswordRememberDataClass)

    @Update
    fun updateDeviceSSIDPWD(passwordRememberDataClass: PasswordRememberDataClass)

    @Query("SELECT * FROM passwordRememberDataClass WHERE device_ssid LIKE :ssid")
    fun getPasswordWithSSID(ssid: String): PasswordRememberDataClass


    @Query("SELECT * FROM passwordRememberDataClass ORDER BY Id DESC")
    fun getAllSSISandPWDS(): List<PasswordRememberDataClass>

    @Query("DELETE FROM passwordRememberDataClass WHERE device_ssid = :device_ssid")
    fun deletePassword(device_ssid: String)
}