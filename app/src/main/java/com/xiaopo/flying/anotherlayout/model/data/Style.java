package com.xiaopo.flying.anotherlayout.model.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.xiaopo.flying.anotherlayout.kits.DB;
import com.xiaopo.flying.anotherlayout.kits.guava.Optional;
import com.xiaopo.flying.anotherlayout.ui.widget.PhotoPuzzleView;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.PuzzlePiece;

import io.reactivex.functions.Function;

import java.util.List;

/**
 * @author wupanjie
 */
public class Style implements Parcelable {
  private final long id;
  private long createAt;
  private long updateAt;
  private String layoutInfo;
  private String pieceInfo;

  private Optional<PuzzleLayout.Info> layout = Optional.absent();
  private Optional<PhotoPuzzleView.PieceInfos> pieces = Optional.absent();

  public static final Function<Cursor, Style> MAPPER = cursor -> {
    long id = DB.getLong(cursor, StyleEntry.ID);
    long createAt = DB.getLong(cursor, StyleEntry.CREATE_AT);
    long updateAt = DB.getLong(cursor, StyleEntry.UPDATE_AT);
    String layoutInfo = DB.getString(cursor, StyleEntry.LAYOUT_INFO);
    String pieceInfo = DB.getString(cursor, StyleEntry.PIECE_INFO);
    return new Style(id, createAt, updateAt, layoutInfo, pieceInfo);
  };

  public Style(long id, long createAt, long updateAt, String layoutInfo, String pieceInfo) {
    this.id = id;
    this.createAt = createAt;
    this.updateAt = updateAt;
    this.layoutInfo = layoutInfo;
    this.pieceInfo = pieceInfo;
  }

  protected Style(Parcel in) {
    id = in.readLong();
    createAt = in.readLong();
    updateAt = in.readLong();
    layoutInfo = in.readString();
    pieceInfo = in.readString();
  }

  public static final Creator<Style> CREATOR = new Creator<Style>() {
    @Override
    public Style createFromParcel(Parcel in) {
      return new Style(in);
    }

    @Override
    public Style[] newArray(int size) {
      return new Style[size];
    }
  };

  public long getId() {
    return id;
  }

  public long getCreateAt() {
    return createAt;
  }

  public void setCreateAt(long createAt) {
    this.createAt = createAt;
  }

  public long getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(long updateAt) {
    this.updateAt = updateAt;
  }

  public String getLayoutInfo() {
    return layoutInfo;
  }

  public void setLayoutInfo(String layoutInfo) {
    this.layoutInfo = layoutInfo;
  }

  public String getPieceInfo() {
    return pieceInfo;
  }

  public void setPieceInfo(String pieceInfo) {
    this.pieceInfo = pieceInfo;
  }

  public Optional<PuzzleLayout.Info> getLayout() {
    return layout;
  }

  public void setLayout(PuzzleLayout.Info layout) {
    this.layout = Optional.fromNullable(layout);
  }

  public Optional<PhotoPuzzleView.PieceInfos> getPieces() {
    return pieces;
  }

  public void setPieces(PhotoPuzzleView.PieceInfos pieces) {
    this.pieces = Optional.fromNullable(pieces);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeLong(id);
    parcel.writeLong(createAt);
    parcel.writeLong(updateAt);
    parcel.writeString(layoutInfo);
    parcel.writeString(pieceInfo);
  }
}
