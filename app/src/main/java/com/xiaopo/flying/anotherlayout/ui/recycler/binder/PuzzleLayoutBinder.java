package com.xiaopo.flying.anotherlayout.ui.recycler.binder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.DebouncedOnClickListener;
import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.anotherlayout.ui.PlaceholderDrawable;
import com.xiaopo.flying.anotherlayout.ui.PlaceholderSelectedDrawable;
import com.xiaopo.flying.anotherlayout.ui.recycler.OnItemClickListener;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.PuzzleView;

import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author wupanjie
 */
public class PuzzleLayoutBinder extends ItemViewBinder<Style, PuzzleLayoutBinder.ViewHolder> {
  public static final int UI_MODE_COMMON = 0;
  public static final int UI_MODE_SELECT = 1;

  private final int screenWidth;
  private int uiMode = UI_MODE_COMMON;
  private final TreeSet<Integer> selectedPositions;

  private OnItemClickListener<Style> onItemClickListener;
  private OnLayoutSelectedListener onLayoutSelectedListener;

  public PuzzleLayoutBinder(TreeSet<Integer> selectedPositions, int screenWidth) {
    this.selectedPositions = selectedPositions;
    this.screenWidth = screenWidth;
  }

  public void setOnItemClickListener(OnItemClickListener<Style> onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  public void setOnLayoutSelectedListener(OnLayoutSelectedListener onLayoutSelectedListener) {
    this.onLayoutSelectedListener = onLayoutSelectedListener;
  }

  public void setUiMode(int uiMode) {
    this.uiMode = uiMode;
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
    ViewGroup.LayoutParams layoutParams = holder.container.getLayoutParams();
    layoutParams.width = screenWidth;
    layoutParams.height = (int) (screenWidth / layoutInfo.width() * layoutInfo.height());
    holder.container.setLayoutParams(layoutParams);

    holder.puzzleView.setTouchEnable(false);
    holder.puzzleView.setLineSize(8);
    holder.puzzleView.setPuzzleLayout(layoutInfo);
    holder.puzzleView.setLineColor(Color.WHITE);
    holder.puzzleView.setQuickMode(true);

    if (layoutInfo.padding == 0) {
      holder.puzzleView.setNeedDrawLine(true);
    } else {
      holder.puzzleView.setNeedDrawLine(false);
    }

    holder.puzzleView.clearPieces();

    if (uiMode == UI_MODE_SELECT) {
      holder.puzzleView.setScaleX(0.9f);
      holder.puzzleView.setScaleY(0.9f);

      for (int i = 0; i < holder.puzzleView.getPuzzleLayout().getAreaCount(); i++) {
        holder.puzzleView.addPiece(item.isSelected() ?
            PlaceholderSelectedDrawable.instance : PlaceholderDrawable.instance);
      }
    } else {
      holder.puzzleView.setScaleX(1f);
      holder.puzzleView.setScaleY(1f);

      for (int i = 0; i < holder.puzzleView.getPuzzleLayout().getAreaCount(); i++) {
        holder.puzzleView.addPiece(PlaceholderDrawable.instance);
      }

    }

    holder.container.setOnClickListener(new DebouncedOnClickListener() {
      @Override public void doClick(View view) {
        if (onItemClickListener != null && uiMode == UI_MODE_COMMON) {
          onItemClickListener.onItemClick(item, holder.getAdapterPosition());
        }

        if (uiMode == UI_MODE_SELECT) {
          if (item.isSelected()) {
            holder.puzzleView.clearPieces();
            for (int i = 0; i < holder.puzzleView.getPuzzleLayout().getAreaCount(); i++) {
              holder.puzzleView.addPiece(PlaceholderDrawable.instance);
            }
            item.setSelected(false);
            selectedPositions.remove(holder.getAdapterPosition());
          } else {
            holder.puzzleView.clearPieces();
            for (int i = 0; i < holder.puzzleView.getPuzzleLayout().getAreaCount(); i++) {
              holder.puzzleView.addPiece(PlaceholderSelectedDrawable.instance);
            }
            item.setSelected(true);
            selectedPositions.add(holder.getAdapterPosition());
          }

          if (onLayoutSelectedListener != null){
            onLayoutSelectedListener.onLayoutSelected(item, item.isSelected());
          }
        }
      }
    });
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.container) ViewGroup container;
    @BindView(R.id.puzzle) PuzzleView puzzleView;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public interface OnLayoutSelectedListener{
    void onLayoutSelected(Style layout, boolean selected);
  }
}
