package com.cumulations.libreV2.roomdatabase;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LibreVoiceDatabase_Impl extends LibreVoiceDatabase {
  private volatile CastLiteDao _castLiteDao;

  private volatile PasswordRememberDao _passwordRememberDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(12) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `uuidDataClass` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `device_ip` TEXT NOT NULL, `request_type` TEXT NOT NULL, `device_uuid` TEXT NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `passwordRememberDataClass` (`Id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `device_ssid` TEXT NOT NULL, `password` TEXT NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'cd78ecc4103237d264efe9cfecb0262a')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `uuidDataClass`");
        _db.execSQL("DROP TABLE IF EXISTS `passwordRememberDataClass`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsUuidDataClass = new HashMap<String, TableInfo.Column>(4);
        _columnsUuidDataClass.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUuidDataClass.put("device_ip", new TableInfo.Column("device_ip", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUuidDataClass.put("request_type", new TableInfo.Column("request_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUuidDataClass.put("device_uuid", new TableInfo.Column("device_uuid", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUuidDataClass = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUuidDataClass = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUuidDataClass = new TableInfo("uuidDataClass", _columnsUuidDataClass, _foreignKeysUuidDataClass, _indicesUuidDataClass);
        final TableInfo _existingUuidDataClass = TableInfo.read(_db, "uuidDataClass");
        if (! _infoUuidDataClass.equals(_existingUuidDataClass)) {
          return new RoomOpenHelper.ValidationResult(false, "uuidDataClass(com.cumulations.libreV2.roomdatabase.CastLiteUUIDDataClass).\n"
                  + " Expected:\n" + _infoUuidDataClass + "\n"
                  + " Found:\n" + _existingUuidDataClass);
        }
        final HashMap<String, TableInfo.Column> _columnsPasswordRememberDataClass = new HashMap<String, TableInfo.Column>(3);
        _columnsPasswordRememberDataClass.put("Id", new TableInfo.Column("Id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPasswordRememberDataClass.put("device_ssid", new TableInfo.Column("device_ssid", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPasswordRememberDataClass.put("password", new TableInfo.Column("password", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPasswordRememberDataClass = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPasswordRememberDataClass = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPasswordRememberDataClass = new TableInfo("passwordRememberDataClass", _columnsPasswordRememberDataClass, _foreignKeysPasswordRememberDataClass, _indicesPasswordRememberDataClass);
        final TableInfo _existingPasswordRememberDataClass = TableInfo.read(_db, "passwordRememberDataClass");
        if (! _infoPasswordRememberDataClass.equals(_existingPasswordRememberDataClass)) {
          return new RoomOpenHelper.ValidationResult(false, "passwordRememberDataClass(com.cumulations.libreV2.roomdatabase.PasswordRememberDataClass).\n"
                  + " Expected:\n" + _infoPasswordRememberDataClass + "\n"
                  + " Found:\n" + _existingPasswordRememberDataClass);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "cd78ecc4103237d264efe9cfecb0262a", "fd0ac760784e7ac7c93f25cb180e23b6");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "uuidDataClass","passwordRememberDataClass");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `uuidDataClass`");
      _db.execSQL("DELETE FROM `passwordRememberDataClass`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CastLiteDao.class, CastLiteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PasswordRememberDao.class, PasswordRememberDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  public List<Migration> getAutoMigrations(
      @NonNull Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecsMap) {
    return Arrays.asList();
  }

  @Override
  public CastLiteDao castLiteDao() {
    if (_castLiteDao != null) {
      return _castLiteDao;
    } else {
      synchronized(this) {
        if(_castLiteDao == null) {
          _castLiteDao = new CastLiteDao_Impl(this);
        }
        return _castLiteDao;
      }
    }
  }

  @Override
  public PasswordRememberDao passwordRememberDao() {
    if (_passwordRememberDao != null) {
      return _passwordRememberDao;
    } else {
      synchronized(this) {
        if(_passwordRememberDao == null) {
          _passwordRememberDao = new PasswordRememberDao_Impl(this);
        }
        return _passwordRememberDao;
      }
    }
  }
}
