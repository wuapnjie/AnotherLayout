package com.xiaopo.flying.anotherlayout.kits;

import com.google.gson.Gson;

/**
 * @author wupanjie
 */
public final class GsonManager {
  private static Gson defaultGson;

  private GsonManager() {

  }

  public static Gson defaultGson() {
    if (defaultGson == null) {
      synchronized (GsonManager.class) {
        if (defaultGson == null) {
          defaultGson = new Gson();
        }
      }
    }

    return defaultGson;
  }

  public static Gson newGson() {
    return new Gson();
  }
}
