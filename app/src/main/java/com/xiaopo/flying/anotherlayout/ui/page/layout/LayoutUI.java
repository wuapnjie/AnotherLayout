package com.xiaopo.flying.anotherlayout.ui.page.layout;

import android.content.Context;
import android.content.Intent;
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
import com.xiaopo.flying.anotherlayout.ui.page.process.ProcessActivity;
import com.xiaopo.flying.anotherlayout.ui.recycler.LoadMoreDelegate;
import com.xiaopo.flying.anotherlayout.ui.recycler.OnItemClickListener;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.ListFooterBinder;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.PuzzleLayoutBinder;
import com.xiaopo.flying.anotherlayout.ui.recycler.decoration.LinearDividerDecoration;

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
public class LayoutUI implements ILayoutUI, OnItemClickListener<Style>, LoadMoreDelegate.LoadMoreSubject, PuzzleLayoutBinder.OnLayoutSelectedListener {
  private static final int UI_MODE_COMMON = 1001;
  private static final int UI_MODE_MANAGE = 1002;

  private final LayoutController controller;

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.puzzle_list) RecyclerView puzzleList;
  @BindView(R.id.icon_more) TextView iconMore;
  @BindView(R.id.btn_menu) View btnMenu;

  private MultiTypeAdapter layoutAdapter;
  private PuzzleLayoutBinder puzzleLayoutBinder;
  private boolean loading;

  private Items layoutItems = new Items();
  private ArrayList<Style> layouts = new ArrayList<>();
  private int uiMode = UI_MODE_COMMON;

  private final TreeSet<Integer> selectedPositions = new TreeSet<>();

  LayoutUI(LayoutController controller, View contentView) {
    this.controller = controller;

    ButterKnife.bind(this, contentView);
  }

  @Override public void initUI() {
    final Context context = controller.context();

    puzzleList.setLayoutManager(new LinearLayoutManager(context));

    final int screenWidth = DipPixelKit.getDeviceWidth(context);
    layoutAdapter = new MultiTypeAdapter(layoutItems);

    puzzleLayoutBinder = new PuzzleLayoutBinder(selectedPositions, screenWidth);
    puzzleLayoutBinder.setOnItemClickListener(this);
    puzzleLayoutBinder.setOnLayoutSelectedListener(this);
    layoutAdapter.register(Style.class, puzzleLayoutBinder);
    layoutAdapter.register(ListFooter.class, new ListFooterBinder());
    puzzleList.setAdapter(layoutAdapter);

    LinearDividerDecoration decoration = new LinearDividerDecoration(
        context.getResources(),
        R.color.divider_color,
        DipPixelKit.dip2px(context, 16f),
        LinearLayoutManager.VERTICAL);
    puzzleList.addItemDecoration(decoration);

    toolbar.setNavigationOnClickListener(view -> controller.onBackPressed());
    LoadMoreDelegate loadMoreDelegate = new LoadMoreDelegate(this);
    loadMoreDelegate.attach(puzzleList);

    btnMenu.setOnClickListener(v -> {
      if (uiMode == UI_MODE_COMMON) {
        changeToManageScene();
      } else {
        if (!layouts.isEmpty() && selectedPositions.isEmpty()) {
          Toasts.show(context, R.string.no_layout_select);
          return;
        }
        ArrayList<Style> needDelete = new ArrayList<>(selectedPositions.size());
        for (Integer selectedPosition : selectedPositions) {
          needDelete.add(layouts.get(selectedPosition));
        }
        controller.deleteLayouts(needDelete);
      }
    });
  }

  // TODO
  @Override public void changeToCommonScene() {
    uiMode = UI_MODE_COMMON;
    iconMore.setText(R.string.action_manage);
    toolbar.setTitle(R.string.my_layout);

    puzzleLayoutBinder.setUiMode(PuzzleLayoutBinder.UI_MODE_COMMON);
    // TODO 除去选中状态，考虑是否保留
    for (Integer position : selectedPositions) {
      layouts.get(position).setSelected(false);
    }
    selectedPositions.clear();

    layoutAdapter.notifyDataSetChanged();
  }

  @Override public void changeToManageScene() {
    uiMode = UI_MODE_MANAGE;
    iconMore.setText(R.string.action_delete);

    toolbar.setTitle("" + selectedPositions.size());
    puzzleLayoutBinder.setUiMode(PuzzleLayoutBinder.UI_MODE_SELECT);
    layoutAdapter.notifyDataSetChanged();
  }

  @Override public void onDestroy() {

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

  @Override public void onItemClick(Style item, int position) {
    Intent intent = new Intent(controller.context(), ProcessActivity.class);
    intent.putExtra(ProcessActivity.INTENT_KEY_STYLE, item);
    controller.context().startActivity(intent);
  }

  @Override public void addAndShowLayouts(List<Style> layouts) {
    final int insert = layoutItems.size();
    layoutItems.addAll(layouts);
    layoutAdapter.notifyItemInserted(insert);
    this.layouts.addAll(layouts);
  }

  @Override public void notifyNoMore() {
    if (layoutAdapter.getItemCount() == 0) {
      Toasts.show(controller.context(), R.string.no_layout);
    } else {
      final int insert = layoutItems.size();
      if (layoutItems.get(insert - 1) instanceof ListFooter) return;
      layoutItems.add(new ListFooter(controller.context().getString(R.string.no_more)));
      layoutAdapter.notifyItemInserted(insert);
    }
  }

  @Override public void deleteSuccess(List<Style> styles) {
    final int size = styles.size();
    for (int i = 0; i < size; i++) {
      layoutItems.remove(styles.get(i));
      layouts.remove(styles.get(i));
    }

    changeToCommonScene();
  }

  @Override public boolean isLoading() {
    return loading;
  }

  @Override public void onLoadMore() {
    controller.fetchMyLayouts();
  }

  @Override public void onLayoutSelected(Style layout, boolean selected) {
    toolbar.setTitle("" + selectedPositions.size());
  }
}
