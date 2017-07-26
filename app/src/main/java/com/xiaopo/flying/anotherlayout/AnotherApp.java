package com.xiaopo.flying.anotherlayout;

import android.app.Application;
import android.graphics.Bitmap;
import com.squareup.picasso.Picasso;

/**
 * @author wupanjie
 */

public class AnotherApp extends Application {
  @Override public void onCreate() {
    super.onCreate();
    Picasso picasso = new Picasso.Builder(this).defaultBitmapConfig(Bitmap.Config.RGB_565).build();
    Picasso.setSingletonInstance(picasso);
  }
}
