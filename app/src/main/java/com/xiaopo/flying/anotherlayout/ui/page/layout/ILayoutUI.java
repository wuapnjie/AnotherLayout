package com.xiaopo.flying.anotherlayout.ui.page.layout;

import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.anotherlayout.ui.UI;

import java.util.List;

/**
 * @author wupanjie
 */
public interface ILayoutUI extends UI{
  void setLoading(boolean loading);

  void addAndShowLayouts(List<Style> layouts);

  void notifyNoMore();

  void changeToCommonScene();

  void changeToManageScene();

  void deleteSuccess(List<Style> layouts);
}
