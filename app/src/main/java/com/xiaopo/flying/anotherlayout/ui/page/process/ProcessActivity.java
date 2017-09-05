package com.xiaopo.flying.anotherlayout.ui.page.process;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.Colors;
import com.xiaopo.flying.anotherlayout.kits.FileKit;
import com.xiaopo.flying.anotherlayout.kits.PuzzleKit;
import com.xiaopo.flying.anotherlayout.kits.Toasts;
import com.xiaopo.flying.anotherlayout.model.ColorItem;
import com.xiaopo.flying.anotherlayout.model.HandleItem;
import com.xiaopo.flying.anotherlayout.model.data.Stores;
import com.xiaopo.flying.anotherlayout.ui.AnotherActivity;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.ColorItemBinder;
import com.xiaopo.flying.anotherlayout.ui.widget.HandleContainer;
import com.xiaopo.flying.anotherlayout.ui.widget.PhotoPuzzleView;
import com.xiaopo.flying.pixelcrop.DegreeSeekBar;
import com.xiaopo.flying.poiphoto.Define;
import com.xiaopo.flying.poiphoto.PhotoPicker;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.PuzzleView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wupanjie
 */
public class ProcessActivity extends AnotherActivity implements ProcessController {
  private static final String TAG = "ProcessActivity";

  public static final String INTENT_KEY_PATHS = "photo_path";
  public static final String INTENT_KEY_SIZE = "piece_size";
  public static final String INTENT_KEY_THEME = "theme_id";
  public static final String INTENT_KEY_TYPE = "piece_type";

  private PuzzleLayout puzzleLayout;
  private List<String> bitmapPaths;

  private List<Target> targets = new ArrayList<>();
  private int deviceSize = 0;

  private ProcessUI ui;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_process);
    View contentRootView = findViewById(R.id.root_view);
    ui = new ProcessUI(this, contentRootView);

    ButterKnife.bind(this);

    deviceSize = getResources().getDisplayMetrics().widthPixels;

    int type = getIntent().getIntExtra(INTENT_KEY_TYPE, 0);
    int pieceSize = getIntent().getIntExtra(INTENT_KEY_SIZE, 0);
    int themeId = getIntent().getIntExtra(INTENT_KEY_THEME, 0);
    bitmapPaths = getIntent().getStringArrayListExtra(INTENT_KEY_PATHS);
    puzzleLayout = PuzzleKit.getPuzzleLayout(type, pieceSize, themeId);

    ui.initUI();
    ui.setPuzzleLayout(puzzleLayout);

    loadPhoto();
  }

  private void loadPhoto() {
    final List<Bitmap> pieces = new ArrayList<>();

    final int count = bitmapPaths.size() > puzzleLayout.getAreaCount() ? puzzleLayout.getAreaCount()
        : bitmapPaths.size();

    for (int i = 0; i < count; i++) {
      final String photoPath = bitmapPaths.get(i);
      final Target target = new PathTarget(photoPath) {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
          pieces.add(bitmap);
          if (pieces.size() == count) {
            if (bitmapPaths.size() < puzzleLayout.getAreaCount()) {
              for (int i = 0; i < puzzleLayout.getAreaCount(); i++) {
                ui.addPiece(pieces.get(i % count), this.path);
              }
            } else {
              ui.addPieces(pieces);
            }
          }
          targets.remove(this);
        }
      };

      Picasso.with(this)
          .load("file:///" + photoPath)
          .resize(deviceSize, deviceSize)
          .centerInside()
          .config(Bitmap.Config.RGB_565)
          .into(target);

      targets.add(target);
    }
  }

  private void showSelectedPhotoDialog() {
    PhotoPicker.newInstance().setMaxCount(1).pick(this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Define.DEFAULT_REQUEST_CODE && resultCode == RESULT_OK) {
      List<String> paths = data.getStringArrayListExtra(Define.PATHS);
      String path = paths.get(0);

      final Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//          puzzleView.replace(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
//          Snackbar.make(puzzleView, "Replace Failed!", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
      };

      Picasso.with(this)
          .load("file:///" + path)
          .resize(deviceSize, deviceSize)
          .centerInside()
          .into(target);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ui.onDestroy();
  }

  @Override
  public void saveImage(PuzzleLayout.Info layoutInfo,
                        PhotoPuzzleView.PieceInfos pieceInfos) {
    Bitmap bitmap = ui.createBitmap();
    Disposable disposable
        = FileKit
        .saveImageAndGetPath(this, bitmap, "another_" + System.currentTimeMillis())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(uriPair -> {
          pieceInfos.imagePath = uriPair.first.getPath();
          Stores.instance(this)
              .saveLayoutAndPieces(layoutInfo, pieceInfos)
              .subscribe();
        });

    addDisposable(disposable);

  }

  @Override
  public void saveLayout(PuzzleLayout.Info layoutInfo) {
    Disposable disposable = Stores.instance(this)
        .saveLayout(layoutInfo)
        .subscribe(ui::showSaveSuccess);
    addDisposable(disposable);
  }
}
