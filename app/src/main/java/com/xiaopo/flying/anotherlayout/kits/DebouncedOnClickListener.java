package com.xiaopo.flying.anotherlayout.kits;

import android.view.View;

/**
 * @author wupanjie
 */
public abstract class DebouncedOnClickListener implements View.OnClickListener {
  private static boolean enabled = true;
  private static final long DEFAULT_INTERVAL = 500L;
  private long interval;
  private static final Runnable ENABLE_AGAIN = () -> DebouncedOnClickListener.enabled = true;

  public DebouncedOnClickListener() {
    this(DEFAULT_INTERVAL);
  }

  public DebouncedOnClickListener(long interval) {
    this.interval = interval;
  }

  public final void onClick(View v) {
    if (enabled) {
      enabled = false;
      v.postDelayed(ENABLE_AGAIN, this.interval);
      this.doClick(v);
    }

  }

  public abstract void doClick(View view);

  public long getInterval() {
    return this.interval;
  }

  public void setInterval(long interval) {
    this.interval = interval;
  }

  public static boolean isEnabled() {
    return enabled;
  }

  public static void setEnabled(boolean enabled) {
    DebouncedOnClickListener.enabled = enabled;
  }
}

