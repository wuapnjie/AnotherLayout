package com.xiaopo.flying.anotherlayout.ui.page.layout;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.WeakHandler;
import com.xiaopo.flying.anotherlayout.model.database.Stores;
import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.anotherlayout.ui.AnotherActivity;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @author wupanjie
 */
public class LayoutActivity extends AnotherActivity
    implements LayoutController, WeakHandler.IHandler {

  public static final int LIMIT = 10;
  private static final int WHAT_NO_MORE = 10001;

  private int currentOffset;
  private ILayoutUI ui;

  private WeakHandler handler = new WeakHandler(this);

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_layout);
    View contentView = findViewById(R.id.root_view);

    ui = new LayoutUI(this, contentView);
    setUI(ui);
    ui.initUI();

    fetchMyLayouts(LIMIT, currentOffset);
  }

  @Override public void fetchMyLayouts() {
    fetchMyLayouts(LIMIT, currentOffset);
  }

  private void fetchMyLayouts(int limit, int offset) {
    ui.setLoading(true);

    Stores.instance(this)
        .getAllLayouts(limit, offset)
        .compose(this.bindUntilEvent(ActivityEvent.PAUSE))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(styles -> {
          if (styles == null || styles.isEmpty()) {
            handler.sendEmptyMessage(WHAT_NO_MORE);
            return;
          }
          ui.addAndShowLayouts(styles);
          currentOffset += LIMIT;
          ui.setLoading(false);
        });

  }

  @Override public void deleteLayouts(@NonNull List<Style> layouts) {
    Stores.instance(this)
        .deleteStyles(layouts)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> ui.deleteSuccess(layouts));
  }

  @Override
  public void handleMsg(Message msg) {
    switch (msg.what) {
      case WHAT_NO_MORE:
        ui.notifyNoMore();
        break;
    }
  }
}
