package com.xiaopo.flying.anotherlayout.layout.parser;

import com.google.gson.Gson;
import com.xiaopo.flying.anotherlayout.ui.widget.PhotoPuzzleView;

/**
 * @author wupanjie
 */
public class GsonPiecesParser {
  private final Gson gson;

  public GsonPiecesParser(Gson gson) {
    this.gson = gson;
  }

  public PhotoPuzzleView.PieceInfos parse(String piecesInfo) {
    return gson.fromJson(piecesInfo, PhotoPuzzleView.PieceInfos.class);
  }
}
