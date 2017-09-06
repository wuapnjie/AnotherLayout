package com.xiaopo.flying.anotherlayout.layout.parser;

import com.google.gson.Gson;
import com.xiaopo.flying.anotherlayout.ui.widget.PhotoPuzzleView;

/**
 * @author wupanjie
 */
class GsonPiecesParser {
  private final Gson gson;

  GsonPiecesParser(Gson gson) {
    this.gson = gson;
  }

  PhotoPuzzleView.PieceInfos parse(String piecesInfo) {
    return gson.fromJson(piecesInfo, PhotoPuzzleView.PieceInfos.class);
  }
}
