package com.cumulations.libreV2.roomdatabase;

/**
 * Created By SHAIK
 * 15/JUNE/2023
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\bH\'J\u000e\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\nH\'J\u0010\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\bH\'J\u0010\u0010\r\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\'\u00a8\u0006\u000e"}, d2 = {"Lcom/cumulations/libreV2/roomdatabase/PasswordRememberDao;", "", "addDeviceSSIDPWD", "", "passwordRememberDataClass", "Lcom/cumulations/libreV2/roomdatabase/PasswordRememberDataClass;", "deletePassword", "device_ssid", "", "getAllSSISandPWDS", "", "getPasswordWithSSID", "ssid", "updateDeviceSSIDPWD", "oemsdk_release"})
@androidx.room.Dao
public abstract interface PasswordRememberDao {
    
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    public abstract void addDeviceSSIDPWD(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.roomdatabase.PasswordRememberDataClass passwordRememberDataClass);
    
    @androidx.room.Update
    public abstract void updateDeviceSSIDPWD(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.roomdatabase.PasswordRememberDataClass passwordRememberDataClass);
    
    @androidx.room.Query(value = "SELECT * FROM passwordRememberDataClass WHERE device_ssid LIKE :ssid")
    @org.jetbrains.annotations.NotNull
    public abstract com.cumulations.libreV2.roomdatabase.PasswordRememberDataClass getPasswordWithSSID(@org.jetbrains.annotations.NotNull
    java.lang.String ssid);
    
    @androidx.room.Query(value = "SELECT * FROM passwordRememberDataClass ORDER BY Id DESC")
    @org.jetbrains.annotations.NotNull
    public abstract java.util.List<com.cumulations.libreV2.roomdatabase.PasswordRememberDataClass> getAllSSISandPWDS();
    
    @androidx.room.Query(value = "DELETE FROM passwordRememberDataClass WHERE device_ssid = :device_ssid")
    public abstract void deletePassword(@org.jetbrains.annotations.NotNull
    java.lang.String device_ssid);
}