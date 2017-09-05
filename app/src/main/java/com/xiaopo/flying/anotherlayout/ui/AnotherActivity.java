package com.xiaopo.flying.anotherlayout.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author wupanjie
 */
public abstract class AnotherActivity extends AppCompatActivity implements AppController {
  protected CompositeDisposable disposables = new CompositeDisposable();

  protected void addDisposables(Disposable... disposables) {
    this.disposables.addAll(disposables);
  }

  protected void addDisposable(Disposable disposable) {
    this.disposables.addAll(disposable);
  }

  @Override
  public Context context() {
    return isDestroyed() ? null : this;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    disposables.clear();
  }
}
