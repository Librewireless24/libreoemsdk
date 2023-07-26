package com.cumulations.libreV2.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity.Companion.TAG_SECUREROOM
import com.libreAlexa.LibreEntryPoint
import com.libreAlexa.util.LibreLogger
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

/**
 * Created By SHAIK
 */
@Database(entities = [CastLiteUUIDDataClass::class, PasswordRememberDataClass::class], version = 12, exportSchema = false)
abstract class LibreVoiceDatabase : RoomDatabase() {

    abstract fun castLiteDao(): CastLiteDao
    abstract fun passwordRememberDao(): PasswordRememberDao

    companion object {

        @Volatile
        private var INSTANCE: LibreVoiceDatabase? = null

        fun getDatabase(context: Context): LibreVoiceDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this@Companion) {
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(context)
                }
            }
            // Return database.
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): LibreVoiceDatabase {
            /** SECURING THE DATABASE STARTS */
            val key = LibreEntryPoint().getKey()
            val factory = SupportFactory(SQLiteDatabase.getBytes(key!!.toCharArray()))
            val builder = Room.databaseBuilder(context.applicationContext, LibreVoiceDatabase::class.java, "libre_voice_database")
            builder.fallbackToDestructiveMigration()
            builder.enableMultiInstanceInvalidation()
            builder.setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)
            builder.openHelperFactory(factory)
            return builder.build()
            /** SECURING THE DATABASE ENDS */

            /**Non Secure database  *//*  return Room.databaseBuilder(context.applicationContext,
                 LibreVoiceDatabase::class.java, "libre_voice_database")
                 .fallbackToDestructiveMigration()
                 .build()*/
        }

    }
}