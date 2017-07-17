package com.xiaopo.flying.puzzzzle.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.xiaopo.flying.puzzzzle.R;
import com.xiaopo.flying.puzzzzle.model.PhotoHeader;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */

public class PhotoHeaderBinder extends ItemViewBinder<PhotoHeader, PhotoHeaderBinder.ViewHolder> {

  @NonNull @Override
  protected PhotoHeaderBinder.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
      @NonNull ViewGroup parent) {
    View itemView = inflater.inflate(R.layout.item_photo_header,parent,false);
    return new ViewHolder(itemView);
  }

  @Override protected void onBindViewHolder(@NonNull PhotoHeaderBinder.ViewHolder holder,
      @NonNull PhotoHeader item) {

  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    ViewHolder(View itemView) {
      super(itemView);
    }
  }
}
