package com.xiaopo.flying.anotherlayout.model;

import android.support.annotation.DrawableRes;
import android.view.View;

/**
 * @author wupanjie
 */
public class HandleItem {
  @DrawableRes private final int icon;
  private View handleView;
  private boolean isUsing;

  public HandleItem(int icon) {
    this.icon = icon;
  }

  public int getIcon() {
    return icon;
  }

  public void setUsing(boolean using) {
    isUsing = using;
  }

  public boolean isUsing() {
    return isUsing;
  }

  public View getHandleView() {
    return handleView;
  }

  public void setHandleView(View handleView) {
    this.handleView = handleView;
  }
}
