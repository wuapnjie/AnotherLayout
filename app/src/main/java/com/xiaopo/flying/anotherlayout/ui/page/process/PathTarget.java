package com.xiaopo.flying.anotherlayout.ui.page.process;

import android.graphics.drawable.Drawable;

import com.squareup.picasso.Target;

/**
 * @author wupanjie
 */
public abstract class PathTarget implements Target{

  public final String path;

  public PathTarget(String path){
    this.path = path;
  }

  @Override
  public void onBitmapFailed(Drawable errorDrawable) {

  }

  @Override
  public void onPrepareLoad(Drawable placeHolderDrawable) {

  }
}
