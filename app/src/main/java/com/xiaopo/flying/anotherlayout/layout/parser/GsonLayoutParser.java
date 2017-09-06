package com.xiaopo.flying.anotherlayout.layout.parser;

import com.google.gson.Gson;
import com.xiaopo.flying.puzzle.PuzzleLayout;

/**
 * @author wupanjie
 */
class GsonLayoutParser {
  private final Gson gson;

  GsonLayoutParser(Gson gson) {
    this.gson = gson;
  }

  PuzzleLayout.Info parse(String layoutInfo) {
    return gson.fromJson(layoutInfo, PuzzleLayout.Info.class);
  }
}
