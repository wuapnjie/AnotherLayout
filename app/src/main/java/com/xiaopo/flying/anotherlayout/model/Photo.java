package com.xiaopo.flying.anotherlayout.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author wupanjie
 */

public class Photo implements Parcelable {
  private String path = "";
  private long dataAdded;
  private long dataModified;
  private String bucketId = "";
  private String buckedName = "";
  private boolean isSelected;

  public Photo(String path, long dataAdded, long dataModified, String bucketId, String buckedName) {
    this.path = path;
    this.dataAdded = dataAdded;
    this.dataModified = dataModified;
    this.bucketId = bucketId;
    this.buckedName = buckedName;
  }

  public String getBucketId() {
    return bucketId;
  }

  public void setBucketId(String bucketId) {
    this.bucketId = bucketId;
  }

  public String getBuckedName() {
    return buckedName;
  }

  public void setBuckedName(String buckedName) {
    this.buckedName = buckedName;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public long getDataAdded() {
    return dataAdded;
  }

  public void setDataAdded(long dataAdded) {
    this.dataAdded = dataAdded;
  }

  public long getDataModified() {
    return dataModified;
  }

  public void setDataModified(long dataModified) {
    this.dataModified = dataModified;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.path);
    dest.writeLong(this.dataAdded);
    dest.writeLong(this.dataModified);
    dest.writeString(this.bucketId);
    dest.writeString(this.buckedName);
    dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
  }

  protected Photo(Parcel in) {
    this.path = in.readString();
    this.dataAdded = in.readLong();
    this.dataModified = in.readLong();
    this.bucketId = in.readString();
    this.buckedName = in.readString();
    this.isSelected = in.readByte() != 0;
  }

  public static final Creator<Photo> CREATOR = new Creator<Photo>() {
    @Override public Photo createFromParcel(Parcel source) {
      return new Photo(source);
    }

    @Override public Photo[] newArray(int size) {
      return new Photo[size];
    }
  };

  @Override public boolean equals(Object obj) {
    if (!(obj instanceof Photo)) return false;
    Photo photo = (Photo) obj;
    return photo.path.equals(path)
        && photo.dataAdded == dataAdded
        && photo.dataModified == dataModified
        && photo.bucketId.equals(bucketId)
        && photo.buckedName.equals(buckedName)
        && photo.isSelected == isSelected;
  }

  @Override public int hashCode() {
    int result = 117;
    result += path.hashCode();
    result += (int) (dataAdded ^ (dataAdded >>> 32));
    result += (int) (dataModified ^ (dataModified >>> 32));
    result += bucketId.hashCode();
    result += buckedName.hashCode();
    result += isSelected ? 1 : 0;
    return result;
  }
}
