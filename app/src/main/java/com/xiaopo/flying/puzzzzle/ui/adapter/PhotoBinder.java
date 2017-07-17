package com.xiaopo.flying.puzzzzle.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.xiaopo.flying.poiphoto.datatype.Photo;
import com.xiaopo.flying.puzzzzle.R;
import java.util.Set;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */

public class PhotoBinder extends ItemViewBinder<Photo, PhotoBinder.ViewHolder> {
  private final Set<Integer> selectedPositions;
  private OnPhotoSelectedListener onPhotoSelectedListener;

  public PhotoBinder(Set<Integer> selectedPositions) {
    this.selectedPositions = selectedPositions;
  }

  @NonNull @Override
  protected PhotoBinder.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
      @NonNull ViewGroup parent) {
    View itemView = inflater.inflate(R.layout.item_photo, parent, false);
    return new ViewHolder(itemView);
  }

  @Override public void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Photo cellData) {
    holder.bindCellData(cellData);
  }

  public void setOnPhotoSelectedListener(OnPhotoSelectedListener onPhotoSelectedListener) {
    this.onPhotoSelectedListener = onPhotoSelectedListener;
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_photo) ImageView ivPhoto;
    @BindView(R.id.shadow) View shadow;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    void bindCellData(Photo data) {
      Picasso.with(itemView.getContext())
          .load("file:///" + data.getPath())
          .resize(300, 300)
          .centerInside()
          .into(ivPhoto);
      itemView.setOnClickListener(v -> {
        if (data.isSelected()) {
          shadow.setVisibility(View.GONE);
          data.setSelected(false);
          selectedPositions.remove(position());
        } else {
          shadow.setVisibility(View.VISIBLE);
          data.setSelected(true);
          selectedPositions.add(position());
        }

        if (onPhotoSelectedListener != null) {
          onPhotoSelectedListener.onPhotoSelected(data, position());
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
}
