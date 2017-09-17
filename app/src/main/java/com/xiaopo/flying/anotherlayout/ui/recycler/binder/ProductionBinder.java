package com.xiaopo.flying.anotherlayout.ui.recycler.binder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.DebouncedOnClickListener;
import com.xiaopo.flying.anotherlayout.kits.imageload.ImageEngine;
import com.xiaopo.flying.anotherlayout.model.PieceInfos;
import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.anotherlayout.ui.page.process.ProcessActivity;
import com.xiaopo.flying.puzzle.PuzzleLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */
public class ProductionBinder extends ItemViewBinder<Style, ProductionBinder.ViewHolder> {

  private final int screenSize;

  public ProductionBinder(int screenSize) {
    this.screenSize = screenSize;
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
    layoutParams.width = screenSize;
    layoutParams.height = (int) (screenSize / layoutInfo.width() * layoutInfo.height());
    holder.ivProduction.setLayoutParams(layoutParams);

    PieceInfos pieceInfos = item.getPieces().get();
    final int size = pieceInfos.pieces.size();
    for (int i = 0; i < size; i++) {
      ImageEngine.instance()
          .prefetch(holder.itemView.getContext(),
              pieceInfos.pieces.get(i).path,
              screenSize,
              screenSize);
    }

    ImageEngine.instance()
        .load(holder.itemView.getContext(),
            pieceInfos.imagePath,
            holder.ivProduction,
            layoutParams.width,
            layoutParams.height);

    holder.container.setOnClickListener(new DebouncedOnClickListener() {
      @Override
      public void doClick(View view) {
        Intent intent = new Intent(view.getContext(), ProcessActivity.class);
        intent.putExtra(ProcessActivity.INTENT_KEY_STYLE, item);

        view.getContext().startActivity(intent);
      }
    });
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
