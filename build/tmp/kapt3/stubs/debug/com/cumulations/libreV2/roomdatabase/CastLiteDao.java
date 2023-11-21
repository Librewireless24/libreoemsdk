package com.cumulations.libreV2.roomdatabase;

/**
 * Created By SHAIK
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u0010\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nH\'J\u000e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\fH\'J\u0010\u0010\r\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\nH\'J\u0010\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u0004\u001a\u00020\u0005H\'\u00a8\u0006\u000f"}, d2 = {"Lcom/cumulations/libreV2/roomdatabase/CastLiteDao;", "", "addDeviceUUID", "", "castLiteData", "Lcom/cumulations/libreV2/roomdatabase/CastLiteUUIDDataClass;", "deleteAllDeviceUUID", "", "deleteDeviceUUID", "device_ip", "", "getAllDeviceUUID", "", "getDeviceUUID", "updateDeviceUUID", "libreoemsdk_debug"})
@androidx.room.Dao
public abstract interface CastLiteDao {
    
    @androidx.room.Insert
    public abstract long addDeviceUUID(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.roomdatabase.CastLiteUUIDDataClass castLiteData);
    
    @androidx.room.Query(value = "SELECT * FROM uuidDataClass ORDER BY device_ip DESC")
    @org.jetbrains.annotations.NotNull
    public abstract java.util.List<com.cumulations.libreV2.roomdatabase.CastLiteUUIDDataClass> getAllDeviceUUID();
    
    @androidx.room.Update
    public abstract void updateDeviceUUID(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.roomdatabase.CastLiteUUIDDataClass castLiteData);
    
    @androidx.room.Delete
    public abstract void deleteAllDeviceUUID(@org.jetbrains.annotations.NotNull
    com.cumulations.libreV2.roomdatabase.CastLiteUUIDDataClass castLiteData);
    
    @androidx.room.Query(value = "DELETE FROM uuidDataClass WHERE device_ip = :device_ip")
    public abstract void deleteDeviceUUID(@org.jetbrains.annotations.NotNull
    java.lang.String device_ip);
    
    @androidx.room.Query(value = "SELECT * FROM uuidDataClass WHERE device_ip LIKE :device_ip")
    @org.jetbrains.annotations.NotNull
    public abstract com.cumulations.libreV2.roomdatabase.CastLiteUUIDDataClass getDeviceUUID(@org.jetbrains.annotations.NotNull
    java.lang.String device_ip);
}