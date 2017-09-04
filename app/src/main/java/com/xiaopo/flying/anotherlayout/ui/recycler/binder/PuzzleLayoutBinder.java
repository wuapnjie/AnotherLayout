package com.xiaopo.flying.anotherlayout.ui.recycler.binder;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.anotherlayout.model.data.Style;
import com.xiaopo.flying.anotherlayout.ui.PlaceHolderDrawable;
import com.xiaopo.flying.puzzle.SquarePuzzleView;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */
public class PuzzleLayoutBinder extends ItemViewBinder<Style, PuzzleLayoutBinder.ViewHolder> {

  @NonNull @Override protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
      @NonNull ViewGroup parent) {
    View itemView = inflater.inflate(R.layout.item_my_layout, parent, false);
    return new ViewHolder(itemView);
  }

  @Override protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Style item) {

    final int length = DipPixelKit.getDeviceWidth(holder.itemView.getContext());
    ViewGroup.LayoutParams layoutParams = holder.puzzleView.getLayoutParams();
    layoutParams.width = length;
    layoutParams.height = length;
    holder.puzzleView.setLayoutParams(layoutParams);

    holder.puzzleView.setTouchEnable(false);

    if (!item.getLayout().isPresent()) return;

    holder.puzzleView.setPuzzleLayout(item.getLayout().get());
    for (int i = 0; i < holder.puzzleView.getPuzzleLayout().getAreaCount(); i++) {
      holder.puzzleView.addPiece(PlaceHolderDrawable.instance);
    }
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.container) FrameLayout container;
    @BindView(R.id.puzzle) SquarePuzzleView puzzleView;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
