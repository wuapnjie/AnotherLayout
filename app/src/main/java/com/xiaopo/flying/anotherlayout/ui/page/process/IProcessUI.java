package com.xiaopo.flying.anotherlayout.ui.page.process;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;

import com.xiaopo.flying.anotherlayout.ui.UI;
import com.xiaopo.flying.puzzle.PuzzleLayout;

import java.util.Set;


/**
 * @author wupanjie
 */
public interface IProcessUI extends UI{

  void initHandleBar();

  void setPuzzleLayout(@NonNull PuzzleLayout puzzleLayout);

  PuzzleLayout setPuzzleLayoutInfo(@NonNull PuzzleLayout.Info puzzleLayoutInfo);

  void addPiece(Bitmap piece, String path);

  void addPiece(Bitmap piece, String path, Matrix initialMatrix);

  void showReplaceScene();

  void showReplaceScene(Set<Integer> initialSelectPositions);

  void showSaveSuccess();

  Bitmap createBitmap();

  void showPlaceholder();

}
