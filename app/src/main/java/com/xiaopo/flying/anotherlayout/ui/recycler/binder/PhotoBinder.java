package com.xiaopo.flying.anotherlayout.ui.recycler.binder;

import android.support.annotation.NonNull;
import android.support.v7.util.AsyncListUtil;
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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */

public class PhotoBinder extends ItemViewBinder<Photo, PhotoBinder.ViewHolder> {

  public static final int SELECT_MODE_MULTI = 0;
  public static final int SELECT_MODE_SINGLE = 1;

  private final Set<Integer> selectedPositions;
  private OnPhotoSelectedListener onPhotoSelectedListener;
  private OnPhotoMaxCountListener onPhotoMaxCountListener;
  private int selectedPosition;
  private int maxCount;
  private final int width;
  private final int height;

  private RecyclerView recyclerView;
  private List<Photo> photos;

  private int selectMode;

  public PhotoBinder(Set<Integer> selectedPositions, int maxCount, int width, int height) {
    this.selectedPositions = selectedPositions;
    this.maxCount = maxCount;
    this.width = width;
    this.height = height;
    selectMode = SELECT_MODE_MULTI;
  }

  public PhotoBinder(RecyclerView recyclerView, List<Photo> photos, int width, int height) {
    this(Collections.emptySet(), 1, width, height);
    this.recyclerView = recyclerView;
    this.photos = photos;
    selectMode = SELECT_MODE_SINGLE;
  }

  public void setSelectMode(int selectMode) {
    this.selectMode = selectMode;
  }

  @NonNull
  @Override
  protected PhotoBinder.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
                                                      @NonNull ViewGroup parent) {
    View itemView = inflater.inflate(R.layout.item_photo, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Photo item) {
    int photoWidth = width;
    int photoHeight = height;

    if (photoWidth == -1) {
      photoWidth = (int) (photoHeight * ((float) item.getWidth() / item.getHeight()));
    } else if (photoHeight == -1) {
      photoHeight = (int) (photoWidth * ((float) item.getHeight() / item.getWidth()));
    }

    ViewGroup.LayoutParams layoutParams = holder.photoContainer.getLayoutParams();
    layoutParams.width = photoWidth;
    layoutParams.height = photoHeight;
    holder.photoContainer.setLayoutParams(layoutParams);

    ImageEngine.instance()
        .load(holder.itemView.getContext(),
            item.getPath(),
            holder.ivPhoto,
            photoWidth,
            photoHeight);


    holder.itemView.setOnClickListener(getClickListener(holder, item));

    if (item.isSelected()) {
      holder.shadow.setVisibility(View.VISIBLE);
    } else {
      holder.shadow.setVisibility(View.GONE);
    }
  }

  private View.OnClickListener getClickListener(@NonNull ViewHolder holder, @NonNull Photo item) {
    if (selectMode == SELECT_MODE_MULTI) {
      return view -> {
        if (item.isSelected()) {
          holder.shadow.setVisibility(View.GONE);
          item.setSelected(false);
          selectedPositions.remove(holder.getAdapterPosition());

          if (onPhotoSelectedListener != null) {
            onPhotoSelectedListener.onPhotoSelected(item, holder.getAdapterPosition());
          }
        } else {
          if (selectedPositions.size() >= maxCount) {
            if (onPhotoMaxCountListener != null) {
              onPhotoMaxCountListener.onPhotoMaxCount();
            }
          } else {
            holder.shadow.setVisibility(View.VISIBLE);
            item.setSelected(true);
            selectedPositions.add(holder.getAdapterPosition());

            if (onPhotoSelectedListener != null) {
              onPhotoSelectedListener.onPhotoSelected(item, holder.getAdapterPosition());
            }
          }
        }
      };
    } else if (selectMode == SELECT_MODE_SINGLE) {
      return view -> {
        photos.get(selectedPosition).setSelected(false);

        ViewHolder viewHolder =
            (ViewHolder) recyclerView.findViewHolderForLayoutPosition(selectedPosition);

        if (viewHolder != null) {
          viewHolder.shadow.setVisibility(View.GONE);
        } else {
          getAdapter().notifyItemChanged(selectedPosition);
        }

        holder.shadow.setVisibility(View.VISIBLE);
        item.setSelected(true);
        selectedPosition = getPosition(holder);

        if (onPhotoSelectedListener != null) {
          onPhotoSelectedListener.onPhotoSelected(item, holder.getAdapterPosition());
        }
      };
    }

    return null;
  }

  @Override protected void onBindViewHolder(
      @NonNull ViewHolder holder, @NonNull Photo item, @NonNull List<Object> payloads) {
    super.onBindViewHolder(holder, item, payloads);
    if (payloads.isEmpty()) {
      onBindViewHolder(holder, item);
    } else {
      if (item.isSelected()) {
        holder.shadow.setVisibility(View.VISIBLE);
      } else {
        holder.shadow.setVisibility(View.GONE);
      }
    }
  }

  public void setOnPhotoSelectedListener(OnPhotoSelectedListener onPhotoSelectedListener) {
    this.onPhotoSelectedListener = onPhotoSelectedListener;
  }

  public void setOnPhotoMaxCountListener(OnPhotoMaxCountListener onPhotoMaxCountListener) {
    this.onPhotoMaxCountListener = onPhotoMaxCountListener;
  }

  public void setSelectedPosition(int selectPosition) {
    this.selectedPosition = selectPosition;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_photo) ImageView ivPhoto;
    @BindView(R.id.shadow) View shadow;
    @BindView(R.id.photo_container) FrameLayout photoContainer;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
