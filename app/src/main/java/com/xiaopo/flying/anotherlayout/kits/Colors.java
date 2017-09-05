package com.xiaopo.flying.anotherlayout.kits;

import android.graphics.Color;

import com.xiaopo.flying.anotherlayout.model.ColorItem;

import io.reactivex.annotations.NonNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author wupanjie
 */
@SuppressWarnings("WeakerAccess")
public final class Colors {
  private Colors() {
    //no instance
  }

  public static final ColorItem c1 = new ColorItem(Color.rgb(0, 0, 0));
  public static final ColorItem c2 = new ColorItem(Color.rgb(66, 66, 66));
  public static final ColorItem c3 = new ColorItem(Color.rgb(117, 117, 117));
  public static final ColorItem c4 = new ColorItem(Color.rgb(189, 189, 189));
  public static final ColorItem c5 = new ColorItem(Color.rgb(238, 238, 238));
  public static final ColorItem c6 = new ColorItem(Color.rgb(255, 255, 255));
  public static final ColorItem c7 = new ColorItem(Color.rgb(59, 40, 36));
  public static final ColorItem c8 = new ColorItem(Color.rgb(89, 65, 57));
  public static final ColorItem c9 = new ColorItem(Color.rgb(157, 137, 128));
  public static final ColorItem c10 = new ColorItem(Color.rgb(213, 204, 200));
  public static final ColorItem c11 = new ColorItem(Color.rgb(40, 50, 55));
  public static final ColorItem c12 = new ColorItem(Color.rgb(88, 109, 121));
  public static final ColorItem c13 = new ColorItem(Color.rgb(147, 163, 173));
  public static final ColorItem c14 = new ColorItem(Color.rgb(208, 215, 219));
  public static final ColorItem c15 = new ColorItem(Color.rgb(253, 236, 185));
  public static final ColorItem c16 = new ColorItem(Color.rgb(254, 248, 201));
  public static final ColorItem c17 = new ColorItem(Color.rgb(242, 247, 234));
  public static final ColorItem c18 = new ColorItem(Color.rgb(229, 241, 252));
  public static final ColorItem c19 = new ColorItem(Color.rgb(236, 231, 245));
  public static final ColorItem c20 = new ColorItem(Color.rgb(248, 229, 236));
  public static final ColorItem c21 = new ColorItem(Color.rgb(248, 233, 231));
  public static final ColorItem c22 = new ColorItem(Color.rgb(28, 76, 64));
  public static final ColorItem c23 = new ColorItem(Color.rgb(37, 94, 99));
  public static final ColorItem c24 = new ColorItem(Color.rgb(62, 147, 136));
  public static final ColorItem c25 = new ColorItem(Color.rgb(151, 192, 92));
  public static final ColorItem c26 = new ColorItem(Color.rgb(174, 212, 171));
  public static final ColorItem c27 = new ColorItem(Color.rgb(143, 201, 195));
  public static final ColorItem c28 = new ColorItem(Color.rgb(148, 220, 232));
  public static final ColorItem c29 = new ColorItem(Color.rgb(166, 194, 245));
  public static final ColorItem c30 = new ColorItem(Color.rgb(160, 168, 214));
  public static final ColorItem c31 = new ColorItem(Color.rgb(30, 87, 150));
  public static final ColorItem c32 = new ColorItem(Color.rgb(24, 38, 121));
  public static final ColorItem c33 = new ColorItem(Color.rgb(64, 83, 175));
  public static final ColorItem c34 = new ColorItem(Color.rgb(68, 168, 238));
  public static final ColorItem c35 = new ColorItem(Color.rgb(67, 30, 134));
  public static final ColorItem c36 = new ColorItem(Color.rgb(96, 64, 176));
  public static final ColorItem c37 = new ColorItem(Color.rgb(144, 54, 170));
  public static final ColorItem c38 = new ColorItem(Color.rgb(126, 31, 78));
  public static final ColorItem c39 = new ColorItem(Color.rgb(216, 57, 100));
  public static final ColorItem c40 = new ColorItem(Color.rgb(227, 82, 65));
  public static final ColorItem c41 = new ColorItem(Color.rgb(232, 148, 176));
  public static final ColorItem c42 = new ColorItem(Color.rgb(228, 158, 156));
  public static final ColorItem c43 = new ColorItem(Color.rgb(232, 132, 55));
  public static final ColorItem c44 = new ColorItem(Color.rgb(236, 181, 62));
  public static final ColorItem c45 = new ColorItem(Color.rgb(246, 218, 140));

  @NonNull
  public static List<ColorItem> all() {
    List<ColorItem> items =
        Arrays.asList(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17,
            c18, c19, c20, c21, c22, c23, c24, c25, c26, c27, c28, c29, c30, c31, c32, c33, c34, c35,
            c36, c37, c38, c39, c40, c41, c42, c43, c44, c45);

    for (ColorItem item : items) {
      item.setSelected(false);
    }

    return items;
  }
}
