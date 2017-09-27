package com.xiaopo.flying.anotherlayout.ui.page.production;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.anotherlayout.kits.Toasts;
import com.xiaopo.flying.anotherlayout.kits.WeakHandler;
import com.xiaopo.flying.anotherlayout.model.ListFooter;
import com.xiaopo.flying.anotherlayout.model.database.Stores;
import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.anotherlayout.ui.AnotherActivity;
import com.xiaopo.flying.anotherlayout.ui.recycler.LoadMoreDelegate;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.ListFooterBinder;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.ProductionBinder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wupanjie
 */
public class ProductionActivity extends AnotherActivity
    implements  WeakHandler.IHandler, ProductionController {

  public static final int LIMIT = 10;
  private static final int WHAT_NO_MORE = 10001;

  private IProductionUI ui;

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.image_list) RecyclerView imageList;

  private int currentOffset;
  private WeakHandler handler = new WeakHandler(this);


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_production);
    View contentView = findViewById(R.id.root_view);

    ui = new ProductionUI(this,contentView);
    setUI(ui);
    ui.initUI();

    fetchMyProductions(LIMIT, currentOffset);
  }

  private void fetchMyProductions(int limit, int offset) {
    ui.setLoading(true);

    Stores.instance(this)
        .getAllProductions(limit, offset)
        .compose(this.bindUntilEvent(ActivityEvent.PAUSE))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(styles -> {
          if (styles == null || styles.isEmpty()) {
            handler.sendEmptyMessage(WHAT_NO_MORE);
            return;
          }
          ui.addAndShowProductions(styles);
          currentOffset += LIMIT;
          ui.setLoading(false);
        });

  }

  @Override
  public void handleMsg(Message msg) {
    switch (msg.what) {
      case WHAT_NO_MORE:
        ui.notifyNoMore();
        break;
    }
  }

  @Override public void fetchMyProductions() {
    fetchMyProductions(LIMIT,currentOffset);
  }

  @Override public void deleteProductions(@NonNull List<Style> productions) {
    Stores.instance(this)
        .deleteStyles(productions)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> ui.deleteSuccess(productions));
  }
}
