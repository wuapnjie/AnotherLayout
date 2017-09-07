package com.xiaopo.flying.anotherlayout.ui.recycler.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.model.Album;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */

public class AlbumTitleBinder extends ItemViewBinder<Album, AlbumTitleBinder.ViewHolder> {

  @NonNull
  @Override
  protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
                                          @NonNull ViewGroup parent) {
    View itemView = inflater.inflate(R.layout.item_title, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Album album) {
    holder.bindCellData(album);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_title)
    TextView tvTitle;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    void bindCellData(Album data) {
      tvTitle.setText(data.getName());
    }
  }
}
