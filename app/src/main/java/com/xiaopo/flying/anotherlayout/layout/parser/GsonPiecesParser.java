package com.xiaopo.flying.anotherlayout.layout.parser;

import com.google.gson.Gson;
import com.xiaopo.flying.anotherlayout.model.PieceInfos;

/**
 * @author wupanjie
 */
class GsonPiecesParser {
  private final Gson gson;

  GsonPiecesParser(Gson gson) {
    this.gson = gson;
  }

  PieceInfos parse(String piecesInfo) {
    return gson.fromJson(piecesInfo, PieceInfos.class);
  }
}
