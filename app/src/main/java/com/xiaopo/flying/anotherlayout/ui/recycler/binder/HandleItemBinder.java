package com.xiaopo.flying.anotherlayout.ui.recycler.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.DebouncedOnClickListener;
import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.anotherlayout.model.HandleItem;
import com.xiaopo.flying.anotherlayout.ui.widget.HandleImageView;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */
public class HandleItemBinder extends ItemViewBinder<HandleItem, HandleItemBinder.ViewHolder> {

  private OnItemClickListener onItemClickListener;

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  @NonNull
  @Override
  protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
      @NonNull ViewGroup parent) {
    View itemView = inflater.inflate(R.layout.item_handle_item, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  protected void onBindViewHolder(@NonNull final ViewHolder holder, @NonNull HandleItem item) {
    final int width =
        DipPixelKit.getDeviceWidth(holder.itemView.getContext()) / getAdapter().getItemCount();
    ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
    layoutParams.width = width;
    holder.itemView.setLayoutParams(layoutParams);
    holder.icon.setImageResource(item.getIcon());
    holder.icon.setNeedDrawArrow(item.isUsing());
    holder.itemView.setOnClickListener(new DebouncedOnClickListener() {
      @Override
      public void doClick(View var1) {
        if (onItemClickListener != null) {
          onItemClickListener.onItemClick(holder.icon, item, getPosition(holder));
        }
      }
    });
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.icon) HandleImageView icon;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public interface OnItemClickListener {
    void onItemClick(HandleImageView icon, HandleItem item, int position);
  }
}
