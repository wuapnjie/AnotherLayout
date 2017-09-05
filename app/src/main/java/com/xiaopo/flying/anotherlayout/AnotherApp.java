package com.xiaopo.flying.anotherlayout;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.squareup.picasso.Picasso;

import io.reactivex.plugins.RxJavaPlugins;

/**
 * @author wupanjie
 */

public class AnotherApp extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Picasso picasso =
        new Picasso.Builder(this)
            .defaultBitmapConfig(Bitmap.Config.RGB_565)
            .listener(new Picasso.Listener() {
              @Override
              public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.d("Picasso", "onImageLoadFailed: --> " + exception);
              }
            })
            .build();
    Picasso.setSingletonInstance(picasso);
  }
}
