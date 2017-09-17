package com.xiaopo.flying.anotherlayout.ui.recycler.binder;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.model.ColorItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */
public class ColorItemBinder extends ItemViewBinder<ColorItem, ColorItemBinder.ViewHolder> {
  private OnColorSelectedListener onColorSelectedListener;
  private int selectedPosition;
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

  @Override
  protected void onBindViewHolder(@NonNull final ViewHolder holder, @NonNull final ColorItem item) {
    holder.color.setBackgroundColor(item.getColor());
    if (item.isSelected()) {
      holder.selected.setVisibility(View.VISIBLE);
    } else {
      holder.selected.setVisibility(View.GONE);
    }

    holder.color.setOnClickListener(view -> {

      allColors.get(selectedPosition).setSelected(false);

      ViewHolder viewHolder =
          (ViewHolder) recyclerView.findViewHolderForLayoutPosition(selectedPosition);
      if (viewHolder != null) {
        viewHolder.selected.setVisibility(View.GONE);
      } else {
        getAdapter().notifyItemChanged(selectedPosition);
      }

      holder.selected.setVisibility(View.VISIBLE);
      item.setSelected(true);
      selectedPosition = getPosition(holder);

      if (onColorSelectedListener != null) {
        onColorSelectedListener.onColorSelected(item.getColor());
      }
    });
  }

  @Override
  protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ColorItem item,
                                  @NonNull List<Object> payloads) {
    if (payloads.isEmpty()) {
      onBindViewHolder(holder, item);
    } else {
      if (item.isSelected()) {
        holder.selected.setVisibility(View.VISIBLE);
      } else {
        holder.selected.setVisibility(View.GONE);
      }
    }
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.selected)
    View selected;
    @BindView(R.id.color)
    FrameLayout color;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public interface OnColorSelectedListener {
    void onColorSelected(@ColorInt int color);
  }
}
