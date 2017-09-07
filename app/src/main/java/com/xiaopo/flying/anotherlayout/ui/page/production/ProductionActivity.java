package com.xiaopo.flying.anotherlayout.ui.page.production;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.anotherlayout.kits.Toasts;
import com.xiaopo.flying.anotherlayout.kits.WeakHandler;
import com.xiaopo.flying.anotherlayout.model.database.Stores;
import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.anotherlayout.ui.AnotherActivity;
import com.xiaopo.flying.anotherlayout.ui.page.process.ProcessActivity;
import com.xiaopo.flying.anotherlayout.ui.recycler.LoadMoreDelegate;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.ProductionBinder;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wupanjie
 */
public class ProductionActivity extends AnotherActivity
    implements LoadMoreDelegate.LoadMoreSubject, WeakHandler.IHandler {

  public static final int LIMIT = 10;
  private static final int WHAT_NO_MORE = 10001;

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.image_list)
  RecyclerView imageList;

  private MultiTypeAdapter adapter;
  private Items productionItems = new Items();

  private boolean loading;
  private int currentOffset;
  private WeakHandler handler = new WeakHandler(this);

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_production);
    ButterKnife.bind(this);

    toolbar.setNavigationOnClickListener(v -> onBackPressed());

    imageList.setLayoutManager(new LinearLayoutManager(this));

    final int screenWidth = DipPixelKit.getDeviceWidth(this);
    ProductionBinder productionBinder = new ProductionBinder(screenWidth);
    productionBinder.setOnItemClickListener((item, position) -> {
      Intent intent = new Intent(this, ProcessActivity.class);
      intent.putExtra(ProcessActivity.INTENT_KEY_STYLE, item);
      startActivity(intent);
    });

    adapter = new MultiTypeAdapter(productionItems);
    adapter.register(Style.class, productionBinder);
    imageList.setAdapter(adapter);

    LoadMoreDelegate loadMoreDelegate = new LoadMoreDelegate(this);
    loadMoreDelegate.attach(imageList);

    fetchMyProductions(LIMIT, currentOffset);
  }

  private void fetchMyProductions(int limit, int offset) {
    loading = true;

    Stores.instance(this)
        .getAllProductions(limit, offset)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(styles -> {
          if (styles == null||styles.isEmpty() ){
            handler.sendEmptyMessage(WHAT_NO_MORE);
            return;
          }
          final int insert = productionItems.size();
          productionItems.addAll(styles);
          adapter.notifyItemInserted(insert);
          currentOffset += LIMIT;
          loading = false;
        });
  }

  @Override
  public void handleMsg(Message msg) {
    switch (msg.what) {
      case WHAT_NO_MORE:
        Toasts.show(this, R.string.no_more);
        break;
    }
  }

  @Override
  public boolean isLoading() {
    return loading;
  }

  @Override
  public void onLoadMore() {
    fetchMyProductions(LIMIT, currentOffset);
  }
}
