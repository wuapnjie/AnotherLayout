package com.xiaopo.flying.anotherlayout.kits.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * @author wupanjie
 */
public interface ImageLoadFunction {

  void prefetch(Context context, String path, int width, int height);

  void prefetch(Context context, String path);

  void load(Context context, String path, ImageView imageView, int width, int height);

  void load(Context context, String path, ImageView imageView);

  Bitmap get(Context context, String path, int width, int height) throws Exception;

  Bitmap get(Context context, String path) throws Exception;

}
