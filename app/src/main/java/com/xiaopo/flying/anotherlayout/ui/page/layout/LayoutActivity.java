package com.xiaopo.flying.anotherlayout.ui.page.layout;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.anotherlayout.kits.Toasts;
import com.xiaopo.flying.anotherlayout.kits.WeakHandler;
import com.xiaopo.flying.anotherlayout.model.data.Stores;
import com.xiaopo.flying.anotherlayout.model.data.Style;
import com.xiaopo.flying.anotherlayout.ui.recycler.LoadMoreDelegate;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.PuzzleLayoutBinder;
import com.xiaopo.flying.anotherlayout.ui.recycler.decoration.LinearDividerDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wupanjie
 */
public class LayoutActivity extends AppCompatActivity
    implements LoadMoreDelegate.LoadMoreSubject, WeakHandler.IHandler {

  public static final int LIMIT = 10;
  private static final int WHAT_NO_MORE = 10001;

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.puzzle_list)
  RecyclerView puzzleList;

  private MultiTypeAdapter adapter;
  private boolean loading;
  private int currentOffset;

  private Items layoutItems = new Items();
  private WeakHandler handler = new WeakHandler(this);

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_layout);
    ButterKnife.bind(this);

    puzzleList.setLayoutManager(new LinearLayoutManager(this));

    final int screenWidth = DipPixelKit.getDeviceWidth(this);
    adapter = new MultiTypeAdapter(layoutItems);
    adapter.register(Style.class, new PuzzleLayoutBinder(screenWidth));
    puzzleList.setAdapter(adapter);

    LinearDividerDecoration decoration = new LinearDividerDecoration(
        getResources(),
        R.color.divider_color,
        DipPixelKit.dip2px(this, 16f),
        LinearLayoutManager.VERTICAL);
    puzzleList.addItemDecoration(decoration);

    toolbar.setNavigationOnClickListener(view -> onBackPressed());
    LoadMoreDelegate loadMoreDelegate = new LoadMoreDelegate(this);
    loadMoreDelegate.attach(puzzleList);

    fetchMyLayouts(LIMIT, currentOffset);
  }

  private void fetchMyLayouts(int limit, int offset) {
    loading = true;

    Stores.instance(this)
        .getAllLayouts(limit, offset)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(styles -> {
          if (styles == null||styles.isEmpty() ){
            handler.sendEmptyMessage(WHAT_NO_MORE);
            return;
          }
          final int insert = layoutItems.size();
          layoutItems.addAll(styles);
          adapter.notifyItemInserted(insert);
          currentOffset += LIMIT;
          loading = false;
        });
  }

  @Override
  public boolean isLoading() {
    return loading;
  }

  @Override
  public void onLoadMore() {
    fetchMyLayouts(LIMIT, currentOffset);
  }

  @Override
  public void handleMsg(Message msg) {
    switch (msg.what) {
      case WHAT_NO_MORE:
        Toasts.show(this, R.string.no_more);
        break;
    }
  }
}
