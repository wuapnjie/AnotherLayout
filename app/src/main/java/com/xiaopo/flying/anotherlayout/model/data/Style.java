package com.xiaopo.flying.anotherlayout.model.data;

/**
 * @author wupanjie
 */
public class Style {
  private final int id;
  private long createAt;
  private long updateAt;
  private String layoutInfo;
  private String pieceInfo;

  public Style(int id, long createAt, long updateAt, String layoutInfo, String pieceInfo) {
    this.id = id;
    this.createAt = createAt;
    this.updateAt = updateAt;
    this.layoutInfo = layoutInfo;
    this.pieceInfo = pieceInfo;
  }

  public int getId() {
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
}
