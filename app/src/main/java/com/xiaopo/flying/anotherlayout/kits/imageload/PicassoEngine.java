package com.xiaopo.flying.anotherlayout.kits.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xiaopo.flying.anotherlayout.ui.PlaceholderDrawable;

import java.io.IOException;

/**
 * @author wupanjie
 */
final class PicassoEngine implements ImageLoadFunction {

  private PicassoEngine() {
    //no instance
  }

  public static PicassoEngine instance() {
    return LazyLoad.lazy;
  }

  private static class LazyLoad {
    private static final PicassoEngine lazy = new PicassoEngine();
  }

  @Override
  public void prefetch(Context context, String path, int width, int height) {
    Picasso.with(context)
        .load("file:///" + path)
        .resize(width, height)
        .centerCrop()
        .fetch();
  }

  @Override
  public void prefetch(Context context, String path) {
    Picasso.with(context)
        .load("file:///" + path)
        .centerCrop()
        .fetch();
  }

  @Override
  public void load(Context context, String path, ImageView imageView, int width, int height) {
    Picasso.with(context)
        .load("file:///" + path)
        .placeholder(PlaceholderDrawable.instance)
        .resize(width, height)
        .centerCrop()
        .into(imageView);
  }

  @Override
  public void load(Context context, String path, ImageView imageView) {
    Picasso.with(context)
        .load("file:///" + path)
        .placeholder(PlaceholderDrawable.instance)
        .into(imageView);
  }

  @Override
  public Bitmap get(Context context, String path, int width, int height) throws IOException {
    return Picasso.with(context)
        .load("file:///" + path)
        .resize(width, height)
        .centerCrop()
        .get();
  }

  @Override
  public Bitmap get(Context context, String path) throws IOException {
    return Picasso.with(context)
        .load("file:///" + path)
        .centerCrop()
        .get();
  }
}
