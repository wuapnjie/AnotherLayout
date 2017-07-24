package com.xiaopo.flying.anotherlayout.model.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import java.util.function.Function;

/**
 * @author wupanjie
 */
public class Stores {
  private static Stores instance;
  private BriteDatabase database;
  private Function<Cursor, Style> styleMapperFunction;

  private Stores(Context context) {
    AnotherDBHelper dbHelper = new AnotherDBHelper(context);
    SqlBrite brite = new SqlBrite.Builder().build();
    database = brite.wrapDatabaseHelper(dbHelper, Schedulers.io());
    styleMapperFunction = this::mapStyle;
  }

  private Style mapStyle(Cursor cursor) {
    int id = cursor.getInt(cursor.getColumnIndexOrThrow(StyleEntry.ID));
    long createAt = cursor.getLong(cursor.getColumnIndexOrThrow(StyleEntry.CREATE_AT));
    long updateAt = cursor.getLong(cursor.getColumnIndexOrThrow(StyleEntry.UPDATE_AT));
    String layoutInfo = cursor.getString(cursor.getColumnIndexOrThrow(StyleEntry.LAYOUT_INFO));
    String pieceInfo = cursor.getString(cursor.getColumnIndexOrThrow(StyleEntry.PIECE_INFO));
    return new Style(id, createAt, updateAt, layoutInfo, pieceInfo);
  }

  public static Stores instance(Context context) {
    if (instance == null) {
      synchronized (Stores.class) {
        if (instance == null) {
          instance = new Stores(context);
        }
      }
    }

    return instance;
  }

  public Completable saveStyle(Style style){
    ContentValues values = new ContentValues();
    values.put(StyleEntry.ID,style.getId());
    values.put(StyleEntry.CREATE_AT,style.getCreateAt());
    values.put(StyleEntry.UPDATE_AT,style.getUpdateAt());
    values.put(StyleEntry.LAYOUT_INFO,style.getLayoutInfo());
    values.put(StyleEntry.PIECE_INFO,style.getPieceInfo());
    database.insert(StyleEntry.TABLE_NAME,values, SQLiteDatabase.CONFLICT_REPLACE);
    return Completable.complete();
  }
}
