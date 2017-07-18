package com.xiaopo.flying.anotherlayout.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.anotherlayout.model.HandleItem;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.HandleItemBinder;
import java.util.ArrayList;
import java.util.List;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wupanjie
 */
public class HandleContainer extends LinearLayout implements HandleItemBinder.OnItemClickListener {
  @BindView(R.id.handle_content_layout) FrameLayout handleContentLayout;
  @BindView(R.id.handle_item_list) RecyclerView handleItemList;
  private MultiTypeAdapter handleAdapter;
  @NonNull private List<HandleItem> handleItems = new ArrayList<>();
  private HandleItem currentUsing;

  public HandleContainer(Context context) {
    this(context, null);
  }

  public HandleContainer(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HandleContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setOrientation(VERTICAL);
    inflate(getContext(), R.layout.handle_container, this);
    ButterKnife.bind(this);

    handleAdapter = new MultiTypeAdapter();
    HandleItemBinder binder = new HandleItemBinder();
    binder.setOnItemClickListener(this);
    handleAdapter.register(HandleItem.class, binder);
    handleItemList.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    handleItemList.setAdapter(handleAdapter);
  }

  public void setHandleItems(@NonNull List<HandleItem> handleItems) {
    this.handleItems.clear();
    this.handleItems.addAll(handleItems);
    handleAdapter.setItems(handleItems);
    handleAdapter.notifyDataSetChanged();
  }

  @Override
  public void onItemClick(HandleImageView icon, HandleItem item, int position) {
    if (currentUsing == null) {
      currentUsing = item;
      showHandleDetail(item, icon, position);
    } else {
      if (currentUsing == item) {
        dismissHandleDetail(item, icon, position);
        currentUsing = null;
      } else {
        for (int i = 0; i < handleItems.size(); i++) {
          if (i == position) {
            handleItems.get(i).setUsing(true);
          } else {
            handleItems.get(i).setUsing(false);
          }
        }
        handleAdapter.notifyDataSetChanged();
        currentUsing = item;
        showHandleDetail(item, icon, position);
      }
    }
  }

  private void showHandleDetail(HandleItem item, final HandleImageView icon, final int position) {
    final int width = DipPixelKit.getDeviceWidth(getContext()) / handleItems.size();
    handleContentLayout.setPivotX(width / 2 + position * width);
    handleContentLayout.setPivotY(handleContentLayout.getHeight());

    handleContentLayout.setVisibility(View.VISIBLE);
    handleContentLayout.removeAllViews();
    handleContentLayout.addView(item.getHandleView());
    handleContentLayout.setScaleX(0);
    handleContentLayout.setScaleY(0);
    handleContentLayout.animate()
        .scaleX(1)
        .scaleY(1)
        .setDuration(300)
        .setInterpolator(new DecelerateInterpolator())
        .setListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            icon.setNeedDrawArrow(true);
          }
        })
        .start();
  }

  private void dismissHandleDetail(HandleItem item, final HandleImageView icon, final int position) {
    final int width = DipPixelKit.getDeviceWidth(getContext()) / handleItems.size();
    handleContentLayout.setPivotX(width / 2 + position * width);
    handleContentLayout.setPivotY(handleContentLayout.getHeight());

    handleContentLayout.animate()
        .scaleX(0)
        .scaleY(0)
        .setDuration(300)
        .setInterpolator(new DecelerateInterpolator())
        .setListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            handleContentLayout.setVisibility(View.INVISIBLE);
            icon.setNeedDrawArrow(false);
          }
        })
        .start();
  }
}
