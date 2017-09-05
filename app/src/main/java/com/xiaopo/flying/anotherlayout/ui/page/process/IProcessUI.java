package com.xiaopo.flying.anotherlayout.ui.page.process;

import android.graphics.Bitmap;

import com.xiaopo.flying.anotherlayout.ui.UI;
import com.xiaopo.flying.puzzle.PuzzleLayout;

import java.util.List;

/**
 * @author wupanjie
 */
public interface IProcessUI extends UI{

  void setPuzzleLayout(PuzzleLayout puzzleLayout);

  void addPiece(Bitmap piece, String path);

  void addPieces(List<Bitmap> pieces);

  void showSaveSuccess();

  Bitmap createBitmap();

}
