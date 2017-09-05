package com.xiaopo.flying.anotherlayout.kits;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * @author wupanjie
 */
public final class Toasts {
  private Toasts() {
    //no instance
  }

  public static void show(Context context, @StringRes int resId) {
    Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
  }

  public static void show(Context context, String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }
}
