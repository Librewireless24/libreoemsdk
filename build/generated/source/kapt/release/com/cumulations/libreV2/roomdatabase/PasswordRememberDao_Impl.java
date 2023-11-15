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
public final class PasswordRememberDao_Impl implements PasswordRememberDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PasswordRememberDataClass> __insertionAdapterOfPasswordRememberDataClass;

  private final EntityDeletionOrUpdateAdapter<PasswordRememberDataClass> __updateAdapterOfPasswordRememberDataClass;

  private final SharedSQLiteStatement __preparedStmtOfDeletePassword;

  public PasswordRememberDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPasswordRememberDataClass = new EntityInsertionAdapter<PasswordRememberDataClass>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `passwordRememberDataClass` (`Id`,`device_ssid`,`password`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, PasswordRememberDataClass value) {
        stmt.bindLong(1, value.getId());
        if (value.getDeviceSSID() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getDeviceSSID());
        }
        if (value.getPassword() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getPassword());
        }
      }
    };
    this.__updateAdapterOfPasswordRememberDataClass = new EntityDeletionOrUpdateAdapter<PasswordRememberDataClass>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `passwordRememberDataClass` SET `Id` = ?,`device_ssid` = ?,`password` = ? WHERE `Id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, PasswordRememberDataClass value) {
        stmt.bindLong(1, value.getId());
        if (value.getDeviceSSID() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getDeviceSSID());
        }
        if (value.getPassword() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getPassword());
        }
        stmt.bindLong(4, value.getId());
      }
    };
    this.__preparedStmtOfDeletePassword = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM passwordRememberDataClass WHERE device_ssid = ?";
        return _query;
      }
    };
  }

  @Override
  public void addDeviceSSIDPWD(final PasswordRememberDataClass passwordRememberDataClass) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfPasswordRememberDataClass.insert(passwordRememberDataClass);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateDeviceSSIDPWD(final PasswordRememberDataClass passwordRememberDataClass) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfPasswordRememberDataClass.handle(passwordRememberDataClass);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deletePassword(final String device_ssid) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePassword.acquire();
    int _argIndex = 1;
    if (device_ssid == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, device_ssid);
    }
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeletePassword.release(_stmt);
    }
  }

  @Override
  public PasswordRememberDataClass getPasswordWithSSID(final String ssid) {
    final String _sql = "SELECT * FROM passwordRememberDataClass WHERE device_ssid LIKE ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (ssid == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, ssid);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "Id");
      final int _cursorIndexOfDeviceSSID = CursorUtil.getColumnIndexOrThrow(_cursor, "device_ssid");
      final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
      final PasswordRememberDataClass _result;
      if(_cursor.moveToFirst()) {
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final String _tmpDeviceSSID;
        if (_cursor.isNull(_cursorIndexOfDeviceSSID)) {
          _tmpDeviceSSID = null;
        } else {
          _tmpDeviceSSID = _cursor.getString(_cursorIndexOfDeviceSSID);
        }
        final String _tmpPassword;
        if (_cursor.isNull(_cursorIndexOfPassword)) {
          _tmpPassword = null;
        } else {
          _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
        }
        _result = new PasswordRememberDataClass(_tmpId,_tmpDeviceSSID,_tmpPassword);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<PasswordRememberDataClass> getAllSSISandPWDS() {
    final String _sql = "SELECT * FROM passwordRememberDataClass ORDER BY Id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "Id");
      final int _cursorIndexOfDeviceSSID = CursorUtil.getColumnIndexOrThrow(_cursor, "device_ssid");
      final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
      final List<PasswordRememberDataClass> _result = new ArrayList<PasswordRememberDataClass>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final PasswordRememberDataClass _item;
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final String _tmpDeviceSSID;
        if (_cursor.isNull(_cursorIndexOfDeviceSSID)) {
          _tmpDeviceSSID = null;
        } else {
          _tmpDeviceSSID = _cursor.getString(_cursorIndexOfDeviceSSID);
        }
        final String _tmpPassword;
        if (_cursor.isNull(_cursorIndexOfPassword)) {
          _tmpPassword = null;
        } else {
          _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
        }
        _item = new PasswordRememberDataClass(_tmpId,_tmpDeviceSSID,_tmpPassword);
        _result.add(_item);
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
