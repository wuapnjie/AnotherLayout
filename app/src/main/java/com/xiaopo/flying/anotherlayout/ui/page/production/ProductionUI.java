package com.xiaopo.flying.anotherlayout.ui.page.production;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.anotherlayout.kits.Toasts;
import com.xiaopo.flying.anotherlayout.model.ListFooter;
import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.anotherlayout.ui.recycler.LoadMoreDelegate;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.ListFooterBinder;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.ProductionBinder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wupanjie
 */
public class ProductionUI implements IProductionUI, LoadMoreDelegate.LoadMoreSubject {
  private final ProductionController controller;

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.image_list) RecyclerView imageList;

  private MultiTypeAdapter adapter;
  private Items productionItems = new Items();

  private boolean loading;

  public ProductionUI(ProductionController controller, View contentView) {
    this.controller = controller;

    ButterKnife.bind(this, contentView);
  }

  @Override public void initUI() {
    final Context context = controller.context();
    toolbar.setNavigationOnClickListener(v -> onBackPressed());

    imageList.setLayoutManager(new LinearLayoutManager(context));

    final int screenWidth = DipPixelKit.getDeviceWidth(context);
    ProductionBinder productionBinder = new ProductionBinder(screenWidth);

    adapter = new MultiTypeAdapter(productionItems);
    adapter.register(Style.class, productionBinder);
    adapter.register(ListFooter.class, new ListFooterBinder());
    imageList.setAdapter(adapter);

    LoadMoreDelegate loadMoreDelegate = new LoadMoreDelegate(this);
    loadMoreDelegate.attach(imageList);
  }

  @Override public void addAndShowProductions(List<Style> styles) {
    final int insert = productionItems.size();
    productionItems.addAll(styles);
    adapter.notifyItemInserted(insert);
  }

  @Override public void notifyNoMore() {
    if (adapter.getItemCount() == 0) {
      Toasts.show(controller.context(), R.string.no_production);
    } else {
      final int insert = productionItems.size();
      if (productionItems.get(insert - 1) instanceof ListFooter) return;
      productionItems.add(new ListFooter(controller.context().getString(R.string.no_more)));
      adapter.notifyItemInserted(insert);
    }
  }

  @Override public void onDestroy() {

  }

  @Override public boolean onBackPressed() {
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
}
