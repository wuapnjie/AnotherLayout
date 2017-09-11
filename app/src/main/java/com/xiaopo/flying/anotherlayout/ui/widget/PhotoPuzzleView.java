package com.xiaopo.flying.anotherlayout.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;

import com.xiaopo.flying.anotherlayout.model.PieceInfo;
import com.xiaopo.flying.anotherlayout.model.PieceInfos;
import com.xiaopo.flying.puzzle.PuzzlePiece;
import com.xiaopo.flying.puzzle.PuzzleView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wupanjie
 */
public class PhotoPuzzleView extends PuzzleView {

  public PhotoPuzzleView(Context context) {
    super(context);
  }

  public PhotoPuzzleView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public PhotoPuzzleView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void addPiece(Bitmap bitmap, String path) {
    addPiece(bitmap, null, path);
  }

  public void addPiece(Bitmap bitmap, String path, Matrix initialMatrix) {
    addPiece(bitmap, initialMatrix, path);
  }

  public PieceInfos generatePieceInfo() {
    List<PuzzlePiece> pieces = getPuzzlePieces();
    float[] values = new float[9];
    final int size = pieces.size();
    List<PieceInfo> pieceInfoList = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      Matrix matrix = pieces.get(i).getMatrix();
      matrix.getValues(values);

      PieceInfo pieceInfo = new PieceInfo();
      pieceInfo.path = pieces.get(i).getPath();
      pieceInfo.value1 = values[0];
      pieceInfo.value2 = values[1];
      pieceInfo.value3 = values[2];
      pieceInfo.value4 = values[3];
      pieceInfo.value5 = values[4];
      pieceInfo.value6 = values[5];
      pieceInfo.value7 = values[6];
      pieceInfo.value8 = values[7];
      pieceInfo.value9 = values[8];

      pieceInfoList.add(pieceInfo);
    }

    PieceInfos pieceInfos = new PieceInfos();
    pieceInfos.pieces = pieceInfoList;

    return pieceInfos;
  }
}
