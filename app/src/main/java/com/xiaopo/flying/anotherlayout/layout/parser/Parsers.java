package com.xiaopo.flying.anotherlayout.layout.parser;

import com.xiaopo.flying.anotherlayout.kits.GsonManager;
import com.xiaopo.flying.anotherlayout.ui.widget.PhotoPuzzleView;
import com.xiaopo.flying.puzzle.PuzzleLayout;

/**
 * @author wupanjie
 */
public class Parsers {

  private final GsonLayoutParser layoutParser;
  private final GsonPiecesParser piecesParser;

  private Parsers() {
    this.layoutParser = new GsonLayoutParser(GsonManager.defaultGson());
    this.piecesParser = new GsonPiecesParser(GsonManager.defaultGson());
  }

  public static Parsers instance() {
    return LazyLoad.lazy;
  }

  public PuzzleLayout.Info parseLayout(String layoutInfo) {
    return layoutParser.parse(layoutInfo);
  }

  public PhotoPuzzleView.PieceInfos parsePieces(String piecesInfo) {
    return piecesParser.parse(piecesInfo);
  }

  private static class LazyLoad {
    private static final Parsers lazy = new Parsers();
  }
}
