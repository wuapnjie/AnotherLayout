package com.xiaopo.flying.anotherlayout.kits.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * @author wupanjie
 */
public final class ImageEngine implements ImageLoadFunction {

  private static ImageEngine ENGINE;
  private ImageLoadFunction function;

  private ImageEngine(ImageLoadFunction function) {
    this.function = function;
  }

  public static ImageEngine instance() {
    if (ENGINE == null) {
      synchronized (ImageEngine.class) {
        if (ENGINE == null) {
          ENGINE = new ImageEngine(PicassoEngine.instance());
        }
      }
    }
    return ENGINE;
  }

  public void setImageEngine(ImageLoadFunction function) {
    this.function = function;
  }

  @Override
  public void prefetch(Context context, String path, int width, int height) {
    function.prefetch(context, path, width, height);
  }

  @Override
  public void prefetch(Context context, String path) {
    function.prefetch(context, path);
  }

  @Override
  public void load(Context context, String path, ImageView imageView, int width, int height) {
    function.load(context, path, imageView, width, height);
  }

  @Override
  public void load(Context context, String path, ImageView imageView) {
    function.load(context, path, imageView);
  }

  @Override
  public Bitmap get(Context context, String path, int width, int height) throws Exception {
    return function.get(context, path, width, height);
  }

  @Override
  public Bitmap get(Context context, String path) throws Exception {
    return function.get(context, path);
  }

}
