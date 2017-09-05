package com.xiaopo.flying.anotherlayout.kits;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;

/**
 * @author wupanjie
 */
public final class FileKit {

  private FileKit() {
    //no instance
  }

  public static Observable<Pair<Uri, Uri>> saveImageAndGetPath(Context context, Bitmap bitmap, String name) {
    return Observable.just(bitmap)
        .flatMap(needSave -> {
          Uri privateUri = saveToPrivateDir(context, bitmap, name);
          Uri publicUri = saveToPublicDir(bitmap, name);

          // 通知图库更新
          Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, publicUri);
          context.sendBroadcast(scannerIntent);
          return Observable.just(new Pair<>(privateUri, publicUri));
        });
  }

  private static Uri saveToPrivateDir(Context context, Bitmap bitmap, String name) {
    File appDir = new File(context.getApplicationContext().getCacheDir(), "Another");
    if (!appDir.exists()) {
      appDir.mkdir();
    }
    String fileName = name + ".jpg";
    File file = new File(appDir, fileName);
    try {
      FileOutputStream outputStream = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return Uri.fromFile(file);
  }

  private static Uri saveToPublicDir(Bitmap bitmap, String name) {
    File appDir = new File(Environment.getExternalStorageDirectory(), "Another");
    if (!appDir.exists()) {
      appDir.mkdir();
    }
    String fileName = name + ".jpg";
    File file = new File(appDir, fileName);
    try {
      FileOutputStream outputStream = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return Uri.fromFile(file);
  }
}
