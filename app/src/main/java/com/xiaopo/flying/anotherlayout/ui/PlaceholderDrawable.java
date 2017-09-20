package com.xiaopo.flying.anotherlayout.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author wupanjie
 */
public class PlaceholderDrawable extends Drawable {
  public static Drawable instance = new PlaceholderDrawable();
  private final int color = Color.parseColor("#EDEDED");

  @Override
  public void draw(@NonNull Canvas canvas) {
    canvas.drawColor(color);
  }

  @Override
  public void setAlpha(int alpha) {
    // no-ops
  }

  @Override
  public void setColorFilter(@Nullable ColorFilter colorFilter) {
    // no-ops
  }

  @Override
  public int getOpacity() {
    return PixelFormat.UNKNOWN;
  }

  @Override
  public int getIntrinsicHeight() {
    return 1;
  }

  @Override
  public int getIntrinsicWidth() {
    return 1;
  }
}
