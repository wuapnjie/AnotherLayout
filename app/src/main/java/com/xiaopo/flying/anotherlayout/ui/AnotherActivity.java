package com.xiaopo.flying.anotherlayout.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

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
