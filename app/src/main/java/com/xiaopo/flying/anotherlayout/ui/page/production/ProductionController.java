package com.xiaopo.flying.anotherlayout.ui.page.production;

import android.support.annotation.NonNull;

import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.anotherlayout.ui.AppController;

import java.util.List;

/**
 * @author wupanjie
 */
public interface ProductionController extends AppController {
  void fetchMyProductions();

  void deleteProductions(@NonNull List<Style> productions);
}
