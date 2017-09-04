package com.xiaopo.flying.anotherlayout.layout.parser;

import com.google.gson.Gson;
import com.xiaopo.flying.puzzle.PuzzleLayout;

/**
 * @author wupanjie
 */
public class GsonLayoutParser {
  private final Gson gson;

  public GsonLayoutParser(Gson gson) {
    this.gson = gson;
  }

  public PuzzleLayout.Info parse(String layoutInfo) {
    return gson.fromJson(layoutInfo, PuzzleLayout.Info.class);
  }
}
