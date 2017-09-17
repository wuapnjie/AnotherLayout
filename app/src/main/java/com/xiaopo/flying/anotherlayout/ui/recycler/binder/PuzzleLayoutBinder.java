package com.xiaopo.flying.anotherlayout.ui.recycler.binder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.DebouncedOnClickListener;
import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.anotherlayout.ui.PlaceHolderDrawable;
import com.xiaopo.flying.anotherlayout.ui.recycler.OnItemClickListener;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.PuzzleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */
public class PuzzleLayoutBinder extends ItemViewBinder<Style, PuzzleLayoutBinder.ViewHolder> {

  private final int screenWidth;
  private final OnItemClickListener<Style> onItemClickListener;

  public PuzzleLayoutBinder(int screenWidth, OnItemClickListener<Style> onItemClickListener) {
    this.screenWidth = screenWidth;
    this.onItemClickListener = onItemClickListener;
  }

  @NonNull
  @Override
  protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
                                          @NonNull ViewGroup parent) {
    View itemView = inflater.inflate(R.layout.item_my_layout, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Style item) {
    if (!item.getLayout().isPresent()) return;

    final PuzzleLayout.Info layoutInfo = item.getLayout().get();
    ViewGroup.LayoutParams layoutParams = holder.puzzleView.getLayoutParams();
    layoutParams.width = screenWidth;
    layoutParams.height = (int) (screenWidth / layoutInfo.width() * layoutInfo.height());
    holder.puzzleView.setLayoutParams(layoutParams);

    holder.puzzleView.setTouchEnable(false);
    holder.puzzleView.setLineSize(8);
    holder.puzzleView.setPuzzleLayout(layoutInfo);
    holder.puzzleView.setLineColor(Color.WHITE);
    if (layoutInfo.padding == 0) {
      holder.puzzleView.setNeedDrawLine(true);
    } else {
      holder.puzzleView.setNeedDrawLine(false);
    }
    for (int i = 0; i < holder.puzzleView.getPuzzleLayout().getAreaCount(); i++) {
      holder.puzzleView.addPiece(PlaceHolderDrawable.instance);
    }

    holder.container.setOnClickListener(new DebouncedOnClickListener() {
      @Override public void doClick(View view) {
        if (onItemClickListener != null){
          onItemClickListener.onItemClick(item, holder.getAdapterPosition());
        }
      }
    });
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.puzzle)
    PuzzleView puzzleView;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
