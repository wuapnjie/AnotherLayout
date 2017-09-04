package com.xiaopo.flying.anotherlayout.kits;

import io.reactivex.FlowableTransformer;
import io.reactivex.MaybeTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author wupanjie
 */

public final class IOSwitch {
  private IOSwitch() {
    //no instance
  }

  public static <T> SingleTransformer<T, T> single() {
    return upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
  }

  public static <T> ObservableTransformer<T, T> observable() {
    return upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
  }

  public static <T> MaybeTransformer<T, T> maybe() {
    return upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
  }

  public static <T> FlowableTransformer<T, T> flowable() {
    return upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
  }
}
