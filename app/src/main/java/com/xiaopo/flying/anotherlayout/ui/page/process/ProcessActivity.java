package com.xiaopo.flying.anotherlayout.ui.page.process;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.FileKit;
import com.xiaopo.flying.anotherlayout.kits.PuzzleKit;
import com.xiaopo.flying.anotherlayout.kits.Toasts;
import com.xiaopo.flying.anotherlayout.layout.parser.Parsers;
import com.xiaopo.flying.anotherlayout.model.PieceInfo;
import com.xiaopo.flying.anotherlayout.model.PieceInfos;
import com.xiaopo.flying.anotherlayout.model.database.Stores;
import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.anotherlayout.ui.AnotherActivity;
import com.xiaopo.flying.puzzle.PuzzleLayout;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @author wupanjie
 */
public class ProcessActivity extends AnotherActivity
    implements ProcessController {
  private static final String TAG = "ProcessActivity";

  public static final String INTENT_KEY_PATHS = "photo_path";
  public static final String INTENT_KEY_SIZE = "piece_size";
  public static final String INTENT_KEY_THEME = "theme_id";
  public static final String INTENT_KEY_TYPE = "piece_type";
  public static final String INTENT_KEY_STYLE = "style";

  private PuzzleLayout puzzleLayout;
  private List<String> bitmapPaths;
  private Style style;

  private IProcessUI ui;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_process);

    int type = getIntent().getIntExtra(INTENT_KEY_TYPE, 0);
    int pieceSize = getIntent().getIntExtra(INTENT_KEY_SIZE, 0);
    int themeId = getIntent().getIntExtra(INTENT_KEY_THEME, 0);
    style = getIntent().getParcelableExtra(INTENT_KEY_STYLE);

    bitmapPaths = getIntent().getStringArrayListExtra(INTENT_KEY_PATHS);

    int processMode = ProcessUI.PROCESS_MODE_COMMON;
    if (style != null) {
      if (!TextUtils.isEmpty(style.getPieceInfo())) {
        processMode = ProcessUI.PROCESS_MODE_STYLE;
      } else {
        processMode = ProcessUI.PROCESS_MODE_LAYOUT;
      }
    }
    ViewGroup contentRootView = findViewById(R.id.root_view);

    ui = new ProcessUI(this, contentRootView, processMode);
    setUI(ui);

    ui.initUI();

    if (processMode == ProcessUI.PROCESS_MODE_STYLE) {
      PuzzleLayout.Info layoutInfo = Parsers.instance().parseLayout(style.getLayoutInfo());
      PieceInfos pieceInfos = Parsers.instance().parsePieces(style.getPieceInfo());

      style.setLayout(layoutInfo);
      style.setPieces(pieceInfos);

      puzzleLayout = ui.setPuzzleLayoutInfo(layoutInfo);
      loadPhotoWithStyle();
    } else if (processMode == ProcessUI.PROCESS_MODE_COMMON) {
      puzzleLayout = PuzzleKit.getPuzzleLayout(type, pieceSize, themeId);
      ui.setPuzzleLayout(puzzleLayout);
      loadPhoto();
    } else {
      PuzzleLayout.Info layoutInfo = Parsers.instance().parseLayout(style.getLayoutInfo());

      style.setLayout(layoutInfo);

      puzzleLayout = ui.setPuzzleLayoutInfo(layoutInfo);
      ui.showPlaceholder();
      ui.showReplaceScene();
    }

  }

  private void loadPhotoWithStyle() {
    if (!style.getPieces().isPresent()) return;

    List<PieceInfo> infos = style.getPieces().get().pieces;
    final int count = infos.size();

    LoadPhotosObservables.loadWithStyle(this, style)
        .compose(this.bindToLifecycle())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(results -> {
          for (int i = 0; i < puzzleLayout.getAreaCount(); i++) {
            PieceInfo pieceInfo = infos.get(i);
            float[] values = {
                pieceInfo.value1, pieceInfo.value2, pieceInfo.value3,
                pieceInfo.value4, pieceInfo.value5, pieceInfo.value6,
                pieceInfo.value7, pieceInfo.value8, pieceInfo.value9,
            };

            Matrix matrix = new Matrix();
            matrix.setValues(values);
            ui.addPiece(results.get(i % count).first, results.get(i % count).second, matrix);
          }
        });
  }

  private void loadPhoto() {
    final int count = bitmapPaths.size() > puzzleLayout.getAreaCount()
        ? puzzleLayout.getAreaCount() : bitmapPaths.size();

    LoadPhotosObservables.loadWithPaths(this, puzzleLayout, bitmapPaths)
        .compose(this.bindToLifecycle())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(results -> {
          for (int i = 0; i < puzzleLayout.getAreaCount(); i++) {
            ui.addPiece(results.get(i % count).first, results.get(i % count).second);
          }
        });

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ui.onDestroy();
  }

  @Override
  public void saveImage(PuzzleLayout.Info layoutInfo,
                        PieceInfos pieceInfos) {
    Bitmap bitmap = ui.createBitmap();
    FileKit.saveImageAndGetPath(this, bitmap, "another_" + System.currentTimeMillis())
        .compose(bindToLifecycle())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(uriPair -> {
          pieceInfos.imagePath = uriPair.first.getPath();
          Stores.instance(this)
              .saveLayoutAndPieces(layoutInfo, pieceInfos)
              .compose(this.bindToLifecycle())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(() -> {
                Toasts.show(this, R.string.image_save_success);
              });
        });


  }

  @Override
  public void saveLayout(PuzzleLayout.Info layoutInfo) {
    Stores.instance(this)
        .saveLayout(layoutInfo)
        .compose(this.bindToLifecycle())
        .subscribe(ui::showSaveSuccess);
  }

}
