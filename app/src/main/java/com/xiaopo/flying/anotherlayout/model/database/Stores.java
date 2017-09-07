package com.xiaopo.flying.anotherlayout.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;
import com.xiaopo.flying.anotherlayout.kits.GsonManager;
import com.xiaopo.flying.anotherlayout.layout.parser.Parsers;
import com.xiaopo.flying.anotherlayout.model.PieceInfos;
import com.xiaopo.flying.puzzle.PuzzleLayout;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author wupanjie
 */
public class Stores {
  private static Stores instance;
  private BriteDatabase database;
  private Gson gson;
  private Parsers parsers;

  private Stores(Context context) {
    AnotherDBHelper dbHelper = new AnotherDBHelper(context);
    SqlBrite brite = new SqlBrite.Builder().build();
    database = brite.wrapDatabaseHelper(dbHelper, Schedulers.io());
    gson = GsonManager.defaultGson();
    parsers = Parsers.instance();
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

  public Completable saveStyle(Style style) {
    ContentValues values = new ContentValues();
    values.put(StyleEntry.ID, style.getId());
    values.put(StyleEntry.CREATE_AT, style.getCreateAt());
    values.put(StyleEntry.UPDATE_AT, style.getUpdateAt());
    values.put(StyleEntry.LAYOUT_INFO, style.getLayoutInfo());
    values.put(StyleEntry.PIECE_INFO, style.getPieceInfo());
    database.insert(StyleEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    return Completable.complete();
  }

  public Completable saveLayout(PuzzleLayout.Info layoutInfo) {
    ContentValues values = new ContentValues();
    long currentTimeMillis = System.currentTimeMillis();
    values.put(StyleEntry.CREATE_AT, currentTimeMillis);
    values.put(StyleEntry.UPDATE_AT, currentTimeMillis);
    values.put(StyleEntry.LAYOUT_INFO, gson.toJson(layoutInfo));
    database.insert(StyleEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    return Completable.complete();
  }

  public Completable saveLayoutAndPieces(PuzzleLayout.Info layoutInfo,
                                         PieceInfos pieceInfos) {
    ContentValues values = new ContentValues();
    long currentTimeMillis = System.currentTimeMillis();
    values.put(StyleEntry.CREATE_AT, currentTimeMillis);
    values.put(StyleEntry.UPDATE_AT, currentTimeMillis);
    values.put(StyleEntry.LAYOUT_INFO, gson.toJson(layoutInfo));
    values.put(StyleEntry.PIECE_INFO, gson.toJson(pieceInfos));
    database.insert(StyleEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    return Completable.complete();
  }

  public Observable<List<Style>> getAllStyles() {
    return database.createQuery(StyleEntry.TABLE_NAME,
        "SELECT * " + "FROM " + StyleEntry.TABLE_NAME).mapToList(Style.MAPPER).map(styles -> {
      final int size = styles.size();
      for (int i = 0; i < size; i++) {
        Style style = styles.get(i);
        PuzzleLayout.Info layout = parsers.parseLayout(style.getLayoutInfo());
        style.setLayout(layout);
      }

      return styles;
    });
  }

  public Observable<List<Style>> getAllLayouts(final int limit, final int offset) {
    final String sql
        = "SELECT * " + "FROM " + StyleEntry.TABLE_NAME
        + " WHERE " + StyleEntry.PIECE_INFO + " IS NULL"
        + " LIMIT " + limit + " OFFSET " + offset;

    return database.createQuery(StyleEntry.TABLE_NAME, sql)
        .mapToList(Style.MAPPER)
        .map(styles -> {
          final int size = styles.size();
          for (int i = 0; i < size; i++) {
            Style style = styles.get(i);
            PuzzleLayout.Info layout = parsers.parseLayout(style.getLayoutInfo());
            style.setLayout(layout);
          }

          return styles;
        });
  }

  public Observable<List<Style>> getAllProductions(final int limit, final int offset) {
    final String sql
        = "SELECT * " + "FROM " + StyleEntry.TABLE_NAME
        + " WHERE " + StyleEntry.PIECE_INFO + " IS NOT NULL"
        + " LIMIT " + limit + " OFFSET " + offset;

    return database.createQuery(StyleEntry.TABLE_NAME, sql)
        .mapToList(Style.MAPPER)
        .map(styles -> {
          final int size = styles.size();
          for (int i = 0; i < size; i++) {
            Style style = styles.get(i);
            PuzzleLayout.Info layout = parsers.parseLayout(style.getLayoutInfo());
            PieceInfos piecesInfos = parsers.parsePieces(style.getPieceInfo());
            style.setLayout(layout);
            style.setPieces(piecesInfos);
          }

          return styles;
        });
  }
}
