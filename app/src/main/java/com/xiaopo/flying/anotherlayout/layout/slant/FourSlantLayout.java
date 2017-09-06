package com.xiaopo.flying.anotherlayout.layout.slant;

/**
 * @author wupanjie
 */
public class FourSlantLayout extends NumberSlantLayout {

  public FourSlantLayout(int theme) {
    super(theme);
  }

  @Override
  public int getThemeCount() {
    return 1;
  }

  @Override
  public void layout() {
    switch (theme) {
      case 0:
        cutArea(0, 1, 1);
        break;
    }
  }
}
