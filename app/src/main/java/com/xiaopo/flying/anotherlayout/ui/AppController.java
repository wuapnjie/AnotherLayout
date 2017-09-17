package com.xiaopo.flying.anotherlayout.ui;

import android.content.Context;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

/**
 * @author wupanjie
 */
public interface AppController extends LifecycleProvider<ActivityEvent> {

  void setUI(UI ui);

  void onBackPressed();

  Context context();

}
