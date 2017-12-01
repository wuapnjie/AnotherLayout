package com.xiaopo.flying.anotherlayout;

import android.app.ActivityManager;
import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;
import io.reactivex.functions.Consumer;
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
            .memoryCache(new LruCache(calculateMemoryCacheSize()))
            .listener(new Picasso.Listener() {
              @Override
              public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.d("Picasso", "onImageLoadFailed: --> " + exception);
              }
            })
            .build();
    Picasso.setSingletonInstance(picasso);

    Fabric.with(this, new Crashlytics());

    RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
      @Override public void accept(Throwable throwable) throws Exception {
        Crashlytics.logException(throwable);
      }
    });
  }

  private int calculateMemoryCacheSize() {
    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    int memoryClass = am.getMemoryClass();
    // Target ~33% of the available heap.
    return 1024 * 1024 * memoryClass / 3;
  }
}
