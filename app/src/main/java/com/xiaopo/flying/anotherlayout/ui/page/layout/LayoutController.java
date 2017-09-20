package com.xiaopo.flying.anotherlayout.ui.page.layout;

import android.support.annotation.NonNull;

import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.anotherlayout.ui.AppController;

import java.util.List;

/**
 * @author wupanjie
 */
interface LayoutController extends AppController {
  void fetchMyLayouts();

  void deleteLayouts(@NonNull List<Style> layouts);
}
