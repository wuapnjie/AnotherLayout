package com.xiaopo.flying.anotherlayout.ui.page.process;

import com.xiaopo.flying.anotherlayout.model.PieceInfos;
import com.xiaopo.flying.anotherlayout.ui.AppController;
import com.xiaopo.flying.puzzle.PuzzleLayout;

/**
 * @author wupanjie
 */
public interface ProcessController extends AppController{

  void saveImage(PuzzleLayout.Info layoutInfo,
                 PieceInfos pieceInfo);

  void saveLayout(PuzzleLayout.Info layoutInfo);

}
