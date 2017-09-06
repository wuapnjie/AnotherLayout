package com.xiaopo.flying.anotherlayout.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;

import com.google.gson.annotations.SerializedName;
import com.xiaopo.flying.puzzle.PuzzlePiece;
import com.xiaopo.flying.puzzle.PuzzleView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wupanjie
 */
public class PhotoPuzzleView extends PuzzleView {
  private final List<String> photoPaths = new ArrayList<>();

  public PhotoPuzzleView(Context context) {
    super(context);
  }

  public PhotoPuzzleView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public PhotoPuzzleView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void clearPieces() {
    super.clearPieces();
    photoPaths.clear();
  }

  public void addPiece(Bitmap bitmap, String path) {
    photoPaths.add(path);
    addPiece(bitmap);
  }

  public void addPiece(Bitmap bitmap, String path, Matrix initialMatrix) {
    photoPaths.add(path);
    addPiece(bitmap, initialMatrix);
  }

  public PieceInfos generatePieceInfo() {
    List<PuzzlePiece> pieces = getPuzzlePieces();
    float[] values = new float[9];
    final int size = photoPaths.size();
    List<PieceInfo> pieceInfoList = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      Matrix matrix = pieces.get(i).getMatrix();
      matrix.getValues(values);

      PieceInfo pieceInfo = new PieceInfo();
      pieceInfo.path = photoPaths.get(i);
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

  public static class PieceInfos {
    @SerializedName("pieces")
    public List<PieceInfo> pieces;
    @SerializedName("image_path")
    public String imagePath;
  }

  public static class PieceInfo {
    public String path;
    public float value1;
    public float value2;
    public float value3;
    public float value4;
    public float value5;
    public float value6;
    public float value7;
    public float value8;
    public float value9;
  }
}
