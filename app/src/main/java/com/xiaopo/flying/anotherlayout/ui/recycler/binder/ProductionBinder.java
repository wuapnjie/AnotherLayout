package com.xiaopo.flying.anotherlayout.ui.recycler.binder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.model.data.Style;
import com.xiaopo.flying.anotherlayout.ui.PlaceHolderDrawable;
import com.xiaopo.flying.anotherlayout.ui.widget.PhotoPuzzleView;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.PuzzleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */
public class ProductionBinder extends ItemViewBinder<Style, ProductionBinder.ViewHolder> {

  private final int screenWidth;

  public ProductionBinder(int screenWidth) {
    this.screenWidth = screenWidth;
  }

  @NonNull
  @Override
  protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
                                          @NonNull ViewGroup parent) {
    View itemView = inflater.inflate(R.layout.item_production, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Style item) {
    if (!item.getLayout().isPresent() || !item.getPieces().isPresent()) return;

    final PuzzleLayout.Info layoutInfo = item.getLayout().get();
    ViewGroup.LayoutParams layoutParams = holder.ivProduction.getLayoutParams();
    layoutParams.width = screenWidth;
    layoutParams.height = (int) (screenWidth / layoutInfo.width() * layoutInfo.height());
    holder.ivProduction.setLayoutParams(layoutParams);

    PhotoPuzzleView.PieceInfos pieceInfos = item.getPieces().get();
    Picasso.with(holder.itemView.getContext())
        .load("file:///" + pieceInfos.imagePath)
        .resize(layoutParams.width, layoutParams.height)
        .centerCrop()
        .placeholder(PlaceHolderDrawable.instance)
        .into(holder.ivProduction);

  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.iv_production)
    ImageView ivProduction;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
