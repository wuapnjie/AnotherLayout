package com.xiaopo.flying.anotherlayout.ui.recycler.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.model.ListFooter;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */
public class ListFooterBinder extends ItemViewBinder<ListFooter, ListFooterBinder.ViewHolder> {

  @NonNull @Override protected ViewHolder onCreateViewHolder(
      @NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
    View itemView = inflater.inflate(R.layout.item_list_footer, parent, false);
    return new ViewHolder(itemView);
  }

  @Override protected void onBindViewHolder(
      @NonNull ViewHolder holder, @NonNull ListFooter item) {
    holder.tvHint.setText(item.getHint());
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_hint) TextView tvHint;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
