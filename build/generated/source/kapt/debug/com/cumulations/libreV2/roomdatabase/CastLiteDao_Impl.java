package com.cumulations.libreV2.roomdatabase;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CastLiteDao_Impl implements CastLiteDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CastLiteUUIDDataClass> __insertionAdapterOfCastLiteUUIDDataClass;

  private final EntityDeletionOrUpdateAdapter<CastLiteUUIDDataClass> __deletionAdapterOfCastLiteUUIDDataClass;

  private final EntityDeletionOrUpdateAdapter<CastLiteUUIDDataClass> __updateAdapterOfCastLiteUUIDDataClass;

  private final SharedSQLiteStatement __preparedStmtOfDeleteDeviceUUID;

  public CastLiteDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCastLiteUUIDDataClass = new EntityInsertionAdapter<CastLiteUUIDDataClass>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `uuidDataClass` (`device_ip`,`request_type`,`id`,`device_uuid`) VALUES (?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CastLiteUUIDDataClass value) {
        if (value.getDeviceIP() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getDeviceIP());
        }
        if (value.getRequestType() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getRequestType());
        }
        if (value.getId() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getId());
        }
        if (value.getDeviceUuid() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDeviceUuid());
        }
      }
    };
    this.__deletionAdapterOfCastLiteUUIDDataClass = new EntityDeletionOrUpdateAdapter<CastLiteUUIDDataClass>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `uuidDataClass` WHERE `device_ip` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CastLiteUUIDDataClass value) {
        if (value.getDeviceIP() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getDeviceIP());
        }
      }
    };
    this.__updateAdapterOfCastLiteUUIDDataClass = new EntityDeletionOrUpdateAdapter<CastLiteUUIDDataClass>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `uuidDataClass` SET `device_ip` = ?,`request_type` = ?,`id` = ?,`device_uuid` = ? WHERE `device_ip` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CastLiteUUIDDataClass value) {
        if (value.getDeviceIP() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getDeviceIP());
        }
        if (value.getRequestType() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getRequestType());
        }
        if (value.getId() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getId());
        }
        if (value.getDeviceUuid() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDeviceUuid());
        }
        if (value.getDeviceIP() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getDeviceIP());
        }
      }
    };
    this.__preparedStmtOfDeleteDeviceUUID = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM uuidDataClass WHERE device_ip = ?";
        return _query;
      }
    };
  }

  @Override
  public long addDeviceUUID(final CastLiteUUIDDataClass castLiteData) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfCastLiteUUIDDataClass.insertAndReturnId(castLiteData);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAllDeviceUUID(final CastLiteUUIDDataClass castLiteData) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfCastLiteUUIDDataClass.handle(castLiteData);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateDeviceUUID(final CastLiteUUIDDataClass castLiteData) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfCastLiteUUIDDataClass.handle(castLiteData);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteDeviceUUID(final String device_ip) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteDeviceUUID.acquire();
    int _argIndex = 1;
    if (device_ip == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, device_ip);
    }
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteDeviceUUID.release(_stmt);
    }
  }

  @Override
  public List<CastLiteUUIDDataClass> getAllDeviceUUID() {
    final String _sql = "SELECT * FROM uuidDataClass ORDER BY device_ip DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfDeviceIP = CursorUtil.getColumnIndexOrThrow(_cursor, "device_ip");
      final int _cursorIndexOfRequestType = CursorUtil.getColumnIndexOrThrow(_cursor, "request_type");
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfDeviceUuid = CursorUtil.getColumnIndexOrThrow(_cursor, "device_uuid");
      final List<CastLiteUUIDDataClass> _result = new ArrayList<CastLiteUUIDDataClass>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final CastLiteUUIDDataClass _item;
        final String _tmpDeviceIP;
        if (_cursor.isNull(_cursorIndexOfDeviceIP)) {
          _tmpDeviceIP = null;
        } else {
          _tmpDeviceIP = _cursor.getString(_cursorIndexOfDeviceIP);
        }
        final String _tmpRequestType;
        if (_cursor.isNull(_cursorIndexOfRequestType)) {
          _tmpRequestType = null;
        } else {
          _tmpRequestType = _cursor.getString(_cursorIndexOfRequestType);
        }
        final String _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getString(_cursorIndexOfId);
        }
        final String _tmpDeviceUuid;
        if (_cursor.isNull(_cursorIndexOfDeviceUuid)) {
          _tmpDeviceUuid = null;
        } else {
          _tmpDeviceUuid = _cursor.getString(_cursorIndexOfDeviceUuid);
        }
        _item = new CastLiteUUIDDataClass(_tmpDeviceIP,_tmpRequestType,_tmpId,_tmpDeviceUuid);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public CastLiteUUIDDataClass getDeviceUUID(final String device_ip) {
    final String _sql = "SELECT * FROM uuidDataClass WHERE device_ip LIKE ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (device_ip == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, device_ip);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfDeviceIP = CursorUtil.getColumnIndexOrThrow(_cursor, "device_ip");
      final int _cursorIndexOfRequestType = CursorUtil.getColumnIndexOrThrow(_cursor, "request_type");
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfDeviceUuid = CursorUtil.getColumnIndexOrThrow(_cursor, "device_uuid");
      final CastLiteUUIDDataClass _result;
      if(_cursor.moveToFirst()) {
        final String _tmpDeviceIP;
        if (_cursor.isNull(_cursorIndexOfDeviceIP)) {
          _tmpDeviceIP = null;
        } else {
          _tmpDeviceIP = _cursor.getString(_cursorIndexOfDeviceIP);
        }
        final String _tmpRequestType;
        if (_cursor.isNull(_cursorIndexOfRequestType)) {
          _tmpRequestType = null;
        } else {
          _tmpRequestType = _cursor.getString(_cursorIndexOfRequestType);
        }
        final String _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getString(_cursorIndexOfId);
        }
        final String _tmpDeviceUuid;
        if (_cursor.isNull(_cursorIndexOfDeviceUuid)) {
          _tmpDeviceUuid = null;
        } else {
          _tmpDeviceUuid = _cursor.getString(_cursorIndexOfDeviceUuid);
        }
        _result = new CastLiteUUIDDataClass(_tmpDeviceIP,_tmpRequestType,_tmpId,_tmpDeviceUuid);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
