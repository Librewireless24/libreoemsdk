package com.cumulations.libreV2.roomdatabase;

/**
 * Created By SHAIK
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&\u00a8\u0006\b"}, d2 = {"Lcom/cumulations/libreV2/roomdatabase/LibreVoiceDatabase;", "Landroidx/room/RoomDatabase;", "()V", "castLiteDao", "Lcom/cumulations/libreV2/roomdatabase/CastLiteDao;", "passwordRememberDao", "Lcom/cumulations/libreV2/roomdatabase/PasswordRememberDao;", "Companion", "libreoemsdk_release"})
@androidx.room.Database(entities = {com.cumulations.libreV2.roomdatabase.CastLiteUUIDDataClass.class, com.cumulations.libreV2.roomdatabase.PasswordRememberDataClass.class}, version = 12, exportSchema = false)
public abstract class LibreVoiceDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile
    @org.jetbrains.annotations.Nullable
    private static volatile com.cumulations.libreV2.roomdatabase.LibreVoiceDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.roomdatabase.LibreVoiceDatabase.Companion Companion = null;
    
    public LibreVoiceDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public abstract com.cumulations.libreV2.roomdatabase.CastLiteDao castLiteDao();
    
    @org.jetbrains.annotations.NotNull
    public abstract com.cumulations.libreV2.roomdatabase.PasswordRememberDao passwordRememberDao();
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007H\u0002J\u000e\u0010\b\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u0007H\u0002J\u0018\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\nH\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/cumulations/libreV2/roomdatabase/LibreVoiceDatabase$Companion;", "", "()V", "INSTANCE", "Lcom/cumulations/libreV2/roomdatabase/LibreVoiceDatabase;", "buildDatabase", "context", "Landroid/content/Context;", "getDatabase", "saveAndGenerateKey", "", "updateToSecretKey", "", "secretKey", "libreoemsdk_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.cumulations.libreV2.roomdatabase.LibreVoiceDatabase getDatabase(@org.jetbrains.annotations.NotNull
        android.content.Context context) {
            return null;
        }
        
        private final com.cumulations.libreV2.roomdatabase.LibreVoiceDatabase buildDatabase(android.content.Context context) {
            return null;
        }
        
        private final java.lang.String saveAndGenerateKey(android.content.Context context) {
            return null;
        }
        
        private final void updateToSecretKey(android.content.Context context, java.lang.String secretKey) {
        }
    }
}