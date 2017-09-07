package com.xiaopo.flying.anotherlayout.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author wupanjie
 */
public class AnotherDBHelper extends SQLiteOpenHelper {
  public static final int DATABASE_VERSION = 1;

  public static final String DATABASE_NAME = "AnotherLayout.db";

  private static final String TEXT_TYPE = " TEXT";

  private static final String INTEGER_TYPE = " INTEGER";

  private static final String COMMA_SEP = ",";

  //@formatter:off
  private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
      + StyleEntry.TABLE_NAME + " ("
      + StyleEntry.ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT,"
      + StyleEntry.CREATE_AT + INTEGER_TYPE + COMMA_SEP
      + StyleEntry.UPDATE_AT + INTEGER_TYPE + COMMA_SEP
      + StyleEntry.LAYOUT_INFO + TEXT_TYPE + COMMA_SEP
      + StyleEntry.PIECE_INFO + TEXT_TYPE
      + ")";

  public AnotherDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

  }
}
