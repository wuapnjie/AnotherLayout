package com.xiaopo.flying.anotherlayout.kits.imageload;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.xiaopo.flying.anotherlayout.model.Album;
import com.xiaopo.flying.anotherlayout.model.Photo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wupanjie
 */
public class PhotoManager {
  private final String TAG = PhotoManager.class.getSimpleName();
  private ContentResolver contentResolver;
  private List<String> bucketIds;
  private static final List<Photo> photoCache = new ArrayList<>();

  public PhotoManager(Context context) {
    contentResolver = context.getContentResolver();
    bucketIds = new ArrayList<>();
  }

  public List<Album> getAlbum() {
    bucketIds.clear();

    List<Album> data = new ArrayList<>();
    String projects[] = new String[]{
        MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    };
    Cursor cursor =
        contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projects, null, null,
            MediaStore.Images.Media.DATE_MODIFIED);

    if (cursor != null && cursor.moveToFirst()) {
      do {
        Album album = new Album();

        String buckedId =
            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));

        if (bucketIds.contains(buckedId)) continue;

        bucketIds.add(buckedId);

        String buckedName =
            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
        String coverPath = getFrontCoverData(buckedId);

        album.setId(buckedId);
        album.setName(buckedName);

        data.add(album);
      } while (cursor.moveToNext());

      cursor.close();
    }

    return data;
  }

  public List<Photo> getPhoto(String buckedId, String bucketName) {
    List<Photo> photos = new ArrayList<>();

    Cursor cursor =
        contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_MODIFIED, MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
            }, MediaStore.Images.Media.BUCKET_ID + "=?", new String[]{buckedId},
            MediaStore.Images.Media.DATE_MODIFIED);
    if (cursor != null && cursor.moveToFirst()) {
      do {

        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        Long dataAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
        Long dataModified =
            cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));

        int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
        int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));

        Photo photo = new Photo(path, dataAdded, dataModified, buckedId, bucketName, width, height);

        photos.add(photo);
      } while (cursor.moveToNext());
      cursor.close();
    }

    return photos;
  }

  private String getFrontCoverData(String bucketId) {
    String path = "empty";
    Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        new String[]{MediaStore.Images.Media.DATA}, MediaStore.Images.Media.BUCKET_ID + "=?",
        new String[]{bucketId}, MediaStore.Images.Media.DATE_MODIFIED);
    if (cursor != null && cursor.moveToFirst()) {
      path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
      cursor.close();
    }
    return path;
  }

  public List<Photo> getAllPhotosFromCache() {
    if (photoCache.isEmpty()) {
      return getAllPhotos();
    }

    List<Photo> photos = new ArrayList<>();
    final int size = photoCache.size();
    for (int i = 0; i < size; i++) {
      Photo photo = new Photo(photoCache.get(i));
      photos.add(photo);
    }

    return photos;
  }

  public List<Photo> getAllPhotos() {
    List<Photo> photos = new ArrayList<>();

    String projects[] = new String[]{
        MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.DATE_MODIFIED, MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.WIDTH,
        MediaStore.Images.Media.HEIGHT
    };

    Cursor cursor =
        contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projects, null, null,
            MediaStore.Images.Media.DATE_MODIFIED);
    if (cursor != null && cursor.moveToFirst()) {
      do {

        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        Long dataAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
        Long dataModified =
            cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));

        String bucketId =
            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
        String buckedName =
            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
        int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
        int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));

        Photo photo = new Photo(path, dataAdded, dataModified, bucketId, buckedName, width, height);

        photos.add(photo);
      } while (cursor.moveToNext());
      cursor.close();
    }

    Collections.sort(photos, (lhs, rhs) -> {
      long l = lhs.getDataModified();
      long r = rhs.getDataModified();
      return l > r ? -1 : (l == r ? 0 : 1);
    });

    photoCache.clear();
    photoCache.addAll(photos);

    return photos;
  }
}
