package com.xiaopo.flying.anotherlayout.ui.page.production;

import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.anotherlayout.ui.UI;

import java.util.List;

/**
 * @author wupanjie
 */
public interface IProductionUI extends UI {
  void setLoading(boolean loading);

  void addAndShowProductions(List<Style> styles);

  void notifyNoMore();
}
