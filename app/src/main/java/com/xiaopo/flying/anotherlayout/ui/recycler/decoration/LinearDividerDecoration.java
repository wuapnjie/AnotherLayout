package com.xiaopo.flying.anotherlayout.ui.recycler.decoration;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author wupanjie
 */
public class LinearDividerDecoration extends RecyclerView.ItemDecoration {

  private final Drawable divider;
  private final int size;
  private final int orientation;

  public LinearDividerDecoration(Resources resources, @ColorRes int color, int size, int orientation) {
    this.divider = new ColorDrawable(resources.getColor(color));
    this.size = size;
    this.orientation = orientation;
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    int left;
    int right;
    int top;
    int bottom;
    if (orientation == LinearLayoutManager.HORIZONTAL) {
      top = parent.getPaddingTop();
      bottom = parent.getHeight() - parent.getPaddingBottom();
      final int childCount = parent.getChildCount();
      for (int i = 0; i < childCount - 1; i++) {
        final View child = parent.getChildAt(i);
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        left = child.getRight() + params.rightMargin;
        right = left + size;
        divider.setBounds(left, top, right, bottom);
        divider.draw(c);
      }
    } else {
      left = parent.getPaddingLeft();
      right = parent.getWidth() - parent.getPaddingRight();
      final int childCount = parent.getChildCount();
      for (int i = 0; i < childCount - 1; i++) {
        final View child = parent.getChildAt(i);
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        top = child.getBottom() + params.bottomMargin;
        bottom = top + size;
        divider.setBounds(left, top, right, bottom);
        divider.draw(c);
      }
    }
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    if (orientation == LinearLayoutManager.HORIZONTAL) {
      if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
        return;
      }
      outRect.right = size;
    } else {
      if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
        return;
      }
      outRect.bottom = size;
    }
  }
}
