package com.xiaopo.flying.anotherlayout.ui.recycler.binder;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.model.ColorItem;
import java.util.List;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */
public class ColorItemBinder extends ItemViewBinder<ColorItem, ColorItemBinder.ViewHolder> {
  private OnColorSelectedListener onColorSelectedListener;
  private int currentPosition;
  private final RecyclerView recyclerView;
  private final List<ColorItem> allColors;

  public ColorItemBinder(RecyclerView recyclerView, List<ColorItem> allColors) {
    this.recyclerView = recyclerView;
    this.allColors = allColors;
  }

  public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
    this.onColorSelectedListener = onColorSelectedListener;
  }

  @NonNull
  @Override
  protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
      @NonNull ViewGroup parent) {
    View itemView = inflater.inflate(R.layout.item_color, parent, false);
    return new ViewHolder(itemView);
  }

  // TODO 单选
  @Override
  protected void onBindViewHolder(@NonNull final ViewHolder holder, @NonNull final ColorItem item) {
    holder.color.setBackgroundColor(item.getColor());
    if (item.isSelected()) {
      holder.selected.setVisibility(View.VISIBLE);
    } else {
      holder.selected.setVisibility(View.GONE);
    }

    holder.color.setOnClickListener(view -> {
      if (currentPosition == getPosition(holder)) return;
      ViewHolder viewHolder =
          (ViewHolder) recyclerView.findViewHolderForLayoutPosition(currentPosition);
      if (viewHolder != null) {
        viewHolder.selected.setVisibility(View.GONE);
      }
      allColors.get(currentPosition).setSelected(false);

      holder.selected.setVisibility(View.VISIBLE);
      item.setSelected(true);
      currentPosition = getPosition(holder);

      if (onColorSelectedListener != null) {
        onColorSelectedListener.onColorSelected(item.getColor());
      }
    });
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.selected) View selected;
    @BindView(R.id.color) FrameLayout color;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public interface OnColorSelectedListener {
    void onColorSelected(@ColorInt int color);
  }
}
