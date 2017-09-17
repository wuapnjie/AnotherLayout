package com.xiaopo.flying.anotherlayout.ui.recycler.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.imageload.ImageEngine;
import com.xiaopo.flying.anotherlayout.model.Photo;
import com.xiaopo.flying.anotherlayout.ui.recycler.OnPhotoMaxCountListener;
import com.xiaopo.flying.anotherlayout.ui.recycler.OnPhotoSelectedListener;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */

public class PhotoBinder extends ItemViewBinder<Photo, PhotoBinder.ViewHolder> {

  private final Set<Integer> selectedPositions;
  private OnPhotoSelectedListener onPhotoSelectedListener;
  private OnPhotoMaxCountListener onPhotoMaxCountListener;
  private final int maxCount;
  private final int width;
  private final int height;

  public PhotoBinder(Set<Integer> selectedPositions, int maxCount, int width, int height) {
    this.selectedPositions = selectedPositions;
    this.maxCount = maxCount;
    this.width = width;
    this.height = height;
  }

  @NonNull
  @Override
  protected PhotoBinder.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
                                                      @NonNull ViewGroup parent) {
    View itemView = inflater.inflate(R.layout.item_photo, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Photo cellData) {
    holder.bindCellData(cellData, width, height);
  }

  public void setOnPhotoSelectedListener(OnPhotoSelectedListener onPhotoSelectedListener) {
    this.onPhotoSelectedListener = onPhotoSelectedListener;
  }

  public void setOnPhotoMaxCountListener(OnPhotoMaxCountListener onPhotoMaxCountListener) {
    this.onPhotoMaxCountListener = onPhotoMaxCountListener;
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_photo) ImageView ivPhoto;
    @BindView(R.id.shadow) View shadow;
    @BindView(R.id.photo_container) FrameLayout photoContainer;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    void bindCellData(Photo data, int width, int height) {
      if (width == -1) {
        width = (int) (height * ((float) data.getWidth() / data.getHeight()));
      } else if (height == -1) {
        height = (int) (width * ((float) data.getHeight() / data.getWidth()));
      }

      ViewGroup.LayoutParams layoutParams = photoContainer.getLayoutParams();
      layoutParams.width = width;
      layoutParams.height = height;
      photoContainer.setLayoutParams(layoutParams);

      ImageEngine.instance()
          .load(itemView.getContext(),
              data.getPath(),
              ivPhoto,
              width,
              height);


      itemView.setOnClickListener(view -> {
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
          } else {
            shadow.setVisibility(View.VISIBLE);
            data.setSelected(true);
            selectedPositions.add(position());

            if (onPhotoSelectedListener != null) {
              onPhotoSelectedListener.onPhotoSelected(data, position());
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
}
