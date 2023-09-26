package com.cumulations.libreV2.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity.Companion.TAG_SECUREROOM
import com.libreAlexa.LibreApplication
import com.libreAlexa.LibreEntryPoint
import com.libreAlexa.util.LibreLogger
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
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
            val encryptedPasskey = LibreEntryPoint().getKey()
            LibreLogger.d(TAG_SECUREROOM, "buildDatabase called")
          /*  val encryptedPasskey = "Libre"*/
            val builder = Room.databaseBuilder(context.applicationContext, LibreVoiceDatabase::class.java, "libre_voice_database.db")
            val factory = SupportFactory(SQLiteDatabase.getBytes(encryptedPasskey?.toCharArray()))
            builder.fallbackToDestructiveMigration()
            builder.enableMultiInstanceInvalidation()
            builder.allowMainThreadQueries()
            builder.setJournalMode(JournalMode.AUTOMATIC)
            builder.openHelperFactory(factory)
            return builder.build()
            /** SECURING THE DATABASE ENDS */

            /**Non Secure database  */
       /*   return Room.databaseBuilder(context.applicationContext,
                 LibreVoiceDatabase::class.java, "libre_voice_database")
                 .fallbackToDestructiveMigration()
            .enableMultiInstanceInvalidation()
            .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)
                 .build()*/
        }

        private fun saveAndGenerateKey(context: Context): String {
            val secretKey = LibreApplication().generateKeyForDataStore().toString()
            LibreEntryPoint().setKey(secretKey)
            updateToSecretKey(context = context.applicationContext, secretKey = secretKey)
            return secretKey
        }

        private fun updateToSecretKey(context: Context, secretKey: String) {
            val dbFilePath = context.getDatabasePath("libre_voice_database.db")
            try {
                if (dbFilePath.exists()) {
                    SQLiteDatabase.loadLibs(context)
                    val db = SQLiteDatabase.openDatabase(dbFilePath.absolutePath, LibreEntryPoint
                        ().getKey()!!.toByteArray(), null, SQLiteDatabase.OPEN_READWRITE, object :
                        SQLiteDatabaseHook {
                        override fun preKey(p0: SQLiteDatabase?) {
                        }

                        override fun postKey(p0: SQLiteDatabase?) {
                        }
                    }) { }
                    db.changePassword(secretKey.toCharArray())
                    db.close()
                }
            }catch (ex:Exception){
                ex.printStackTrace()
            }
        }

    }
}