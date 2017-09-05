package com.xiaopo.flying.anotherlayout.ui.page.process;

import com.xiaopo.flying.anotherlayout.ui.AppController;
import com.xiaopo.flying.anotherlayout.ui.widget.PhotoPuzzleView;
import com.xiaopo.flying.puzzle.PuzzleLayout;

import java.util.List;

/**
 * @author wupanjie
 */
public interface ProcessController extends AppController{

  void saveImage(PuzzleLayout.Info layoutInfo,
                 PhotoPuzzleView.PieceInfos pieceInfo);

  void saveLayout(PuzzleLayout.Info layoutInfo);

}
