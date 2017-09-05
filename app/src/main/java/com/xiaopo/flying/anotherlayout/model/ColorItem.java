package com.xiaopo.flying.anotherlayout.model;

import android.support.annotation.ColorInt;

/**
 * @author wupanjie
 */
public class ColorItem {
  @ColorInt
  private int color;
  private boolean isSelected;

  public ColorItem(@ColorInt int color) {
    this.color = color;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }
}
