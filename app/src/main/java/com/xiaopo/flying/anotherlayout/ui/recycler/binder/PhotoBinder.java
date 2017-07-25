package com.xiaopo.flying.anotherlayout.ui.recycler.binder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.xiaopo.flying.anotherlayout.kits.DebouncedOnClickListener;
import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.poiphoto.datatype.Photo;
import com.xiaopo.flying.anotherlayout.R;
import java.util.Set;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */

public class PhotoBinder extends ItemViewBinder<Photo, PhotoBinder.ViewHolder> {
  private final Set<Integer> selectedPositions;
  private OnPhotoSelectedListener onPhotoSelectedListener;
  private OnPhotoMaxCountListener onPhotoMaxCountListener;
  private int resize;
  private final int maxCount;

  public PhotoBinder(Set<Integer> selectedPositions, int maxCount) {
    this.selectedPositions = selectedPositions;
    this.maxCount = maxCount;
  }

  @NonNull @Override
  protected PhotoBinder.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
      @NonNull ViewGroup parent) {
    View itemView = inflater.inflate(R.layout.item_photo, parent, false);
    return new ViewHolder(itemView);
  }

  @Override public void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Photo cellData) {
    holder.bindCellData(cellData, imageResizeWidth(holder.itemView.getContext()));
  }

  public void setOnPhotoSelectedListener(OnPhotoSelectedListener onPhotoSelectedListener) {
    this.onPhotoSelectedListener = onPhotoSelectedListener;
  }

  public void setOnPhotoMaxCountListener(OnPhotoMaxCountListener onPhotoMaxCountListener) {
    this.onPhotoMaxCountListener = onPhotoMaxCountListener;
  }

  private int imageResizeWidth(Context context) {
    if (resize == 0) {
      final int screenWidth = DipPixelKit.getDeviceWidth(context);
      final int availableWidth = screenWidth - 3 * DipPixelKit.dip2px(context, 2);
      resize = availableWidth / 4;
    }
    return resize;
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_photo) ImageView ivPhoto;
    @BindView(R.id.shadow) View shadow;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    void bindCellData(Photo data, int resize) {
      Picasso.with(itemView.getContext())
          .load("file:///" + data.getPath()).resize(resize, resize).centerCrop()
          .into(ivPhoto);
      itemView.setOnClickListener(new DebouncedOnClickListener() {
        @Override
        public void doClick(View view) {
          if (data.isSelected()) {
            shadow.setVisibility(View.GONE);
            data.setSelected(false);
            selectedPositions.remove(position());

            if (onPhotoSelectedListener != null) {
              onPhotoSelectedListener.onPhotoSelected(data, position());
            }
          } else {
            if (selectedPositions.size() >= maxCount) {
              if (onPhotoMaxCountListener != null) {
                onPhotoMaxCountListener.onPhotoMaxCount();
              }
            }else {
              shadow.setVisibility(View.VISIBLE);
              data.setSelected(true);
              selectedPositions.add(position());

              if (onPhotoSelectedListener != null) {
                onPhotoSelectedListener.onPhotoSelected(data, position());
              }
            }
          }
        }
      });

      if (data.isSelected()) {
        shadow.setVisibility(View.VISIBLE);
      } else {
        shadow.setVisibility(View.GONE);
      }
    }

    int position() {
      return getAdapterPosition();
    }
  }

  public interface OnPhotoSelectedListener {
    void onPhotoSelected(Photo photo, int position);
  }

  public interface OnPhotoMaxCountListener {
    void onPhotoMaxCount();
  }
}
