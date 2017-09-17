package com.xiaopo.flying.anotherlayout.ui;

import android.content.Context;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * @author wupanjie
 */
public abstract class AnotherActivity extends RxAppCompatActivity implements AppController{

  private UI ui;

  @Override
  public Context context() {
    return isDestroyed() ? null : this;
  }

  @Override public void onBackPressed() {
    if (!ui.onBackPressed()){
      super.onBackPressed();
    }
  }

  @Override public void setUI(UI ui) {
    this.ui = ui;
  }
}
