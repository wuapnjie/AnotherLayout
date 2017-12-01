package com.xiaopo.flying.anotherlayout.ui.page.production;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.anotherlayout.kits.Toasts;
import com.xiaopo.flying.anotherlayout.model.ListFooter;
import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.anotherlayout.ui.recycler.LoadMoreDelegate;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.ListFooterBinder;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.ProductionBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wupanjie
 */
class ProductionUI implements IProductionUI, LoadMoreDelegate.LoadMoreSubject, ProductionBinder.OnProductionSelectedListener {
  private static final int UI_MODE_COMMON = 1001;
  private static final int UI_MODE_MANAGE = 1002;

  private final ProductionController controller;

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.image_list) RecyclerView imageList;
  @BindView(R.id.icon_more) TextView iconMore;
  @BindView(R.id.btn_menu) View btnMenu;

  private MultiTypeAdapter productionAdapter;
  private ProductionBinder productionBinder;
  private Items productionItems = new Items();

  private boolean loading;
  private int uiMode = UI_MODE_COMMON;

  private ArrayList<Style> productions = new ArrayList<>();
  private final TreeSet<Integer> selectedPositions = new TreeSet<>();

  ProductionUI(ProductionController controller, View contentView) {
    this.controller = controller;

    ButterKnife.bind(this, contentView);
  }

  @Override public void initUI() {
    final Context context = controller.context();
    toolbar.setNavigationOnClickListener(v -> controller.onBackPressed());

    imageList.setLayoutManager(new LinearLayoutManager(context));

    final int screenWidth = DipPixelKit.getDeviceWidth(context);
    productionBinder = new ProductionBinder(selectedPositions, screenWidth);
    productionBinder.setOnProductionSelectedListener(this);

    productionAdapter = new MultiTypeAdapter(productionItems);
    productionAdapter.register(Style.class, productionBinder);
    productionAdapter.register(ListFooter.class, new ListFooterBinder());
    imageList.setAdapter(productionAdapter);

    LoadMoreDelegate loadMoreDelegate = new LoadMoreDelegate(this);
    loadMoreDelegate.attach(imageList);

    btnMenu.setOnClickListener(v -> {
      if (uiMode == UI_MODE_COMMON) {
        changeToManageScene();
      } else {
        if (!productions.isEmpty() && selectedPositions.isEmpty()) {
          Toasts.show(context, R.string.no_production_select);
          return;
        }
        ArrayList<Style> needDelete = new ArrayList<>(selectedPositions.size());
        for (Integer selectedPosition : selectedPositions) {
          needDelete.add(productions.get(selectedPosition));
        }
        if (needDelete.isEmpty()) return;
        controller.deleteProductions(needDelete);
      }
    });
  }

  @Override public void addAndShowProductions(List<Style> styles) {
    final int insert = productionItems.size();
    productionItems.addAll(styles);
    productions.addAll(styles);
    productionAdapter.notifyItemInserted(insert);
  }

  @Override public void notifyNoMore() {
    if (productionAdapter.getItemCount() == 0) {
      Toasts.show(controller.context(), R.string.no_production);
    } else {
      final int insert = productionItems.size();
      if (productionItems.get(insert - 1) instanceof ListFooter) return;
      productionItems.add(new ListFooter(controller.context().getString(R.string.no_more)));
      productionAdapter.notifyItemInserted(insert);
    }
  }

  @Override public void onDestroy() {

  }

  @Override public void changeToCommonScene() {
    uiMode = UI_MODE_COMMON;
    iconMore.setText(R.string.action_manage);
    toolbar.setTitle(R.string.my_layout);

    productionBinder.setUiMode(ProductionBinder.UI_MODE_COMMON);
    // TODO 除去选中状态，考虑是否保留
    for (Style production : productions) {
      production.setSelected(false);
    }
    selectedPositions.clear();

    if (productions.isEmpty()){
      productionItems.clear();
    }

    productionAdapter.notifyDataSetChanged();
  }

  @Override public void changeToManageScene() {
    uiMode = UI_MODE_MANAGE;
    iconMore.setText(R.string.action_delete);

    toolbar.setTitle("" + selectedPositions.size());
    productionBinder.setUiMode(ProductionBinder.UI_MODE_SELECT);
    productionAdapter.notifyDataSetChanged();
  }

  @Override public void deleteSuccess(List<Style> styles) {
    final int size = styles.size();
    for (int i = 0; i < size; i++) {
      productionItems.remove(styles.get(i));
      productions.remove(styles.get(i));
    }

    changeToCommonScene();
  }

  @Override public boolean onBackPressed() {
    if (uiMode == UI_MODE_MANAGE) {
      uiMode = UI_MODE_COMMON;
      changeToCommonScene();
      return true;
    }
    return false;
  }

  @Override public void setLoading(boolean loading) {
    this.loading = loading;
  }

  @Override public boolean isLoading() {
    return loading;
  }

  @Override public void onLoadMore() {
    controller.fetchMyProductions();
  }

  @Override public void onProductionSelected(Style layout, boolean selected) {
    toolbar.setTitle("" + selectedPositions.size());
  }
}
