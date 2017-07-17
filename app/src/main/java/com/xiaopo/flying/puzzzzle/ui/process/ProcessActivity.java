package com.xiaopo.flying.puzzzzle.ui.process;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.xiaopo.flying.pixelcrop.DegreeSeekBar;
import com.xiaopo.flying.poiphoto.Define;
import com.xiaopo.flying.poiphoto.PhotoPicker;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.PuzzleView;
import com.xiaopo.flying.puzzzzle.R;
import com.xiaopo.flying.puzzzzle.kits.PuzzleKit;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wupanjie
 */
public class ProcessActivity extends AppCompatActivity implements View.OnClickListener {
  private static final int FLAG_CONTROL_LINE_SIZE = 1;
  private static final int FLAG_CONTROL_CORNER = 1 << 1;
  public static final String INTENT_KEY_PATHS = "photo_path";
  public static final String INTENT_KEY_SIZE = "piece_size";
  public static final String INTENT_KEY_THEME = "theme_id";
  public static final String INTENT_KEY_TYPE = "piece_type";

  private PuzzleLayout puzzleLayout;
  private List<String> bitmapPaint;
  private PuzzleView puzzleView;
  private DegreeSeekBar degreeSeekBar;

  private List<Target> targets = new ArrayList<>();
  private int deviceWidth = 0;

  private int controlFlag;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_process);

    deviceWidth = getResources().getDisplayMetrics().widthPixels;

    int type = getIntent().getIntExtra(INTENT_KEY_TYPE, 0);
    int pieceSize = getIntent().getIntExtra(INTENT_KEY_SIZE, 0);
    int themeId = getIntent().getIntExtra(INTENT_KEY_THEME, 0);
    bitmapPaint = getIntent().getStringArrayListExtra(INTENT_KEY_PATHS);
    puzzleLayout = PuzzleKit.getPuzzleLayout(type, pieceSize, themeId);

    initView();

    puzzleView.post(this::loadPhoto);
  }

  @Override protected void onResume() {
    super.onResume();
  }

  private void loadPhoto() {
    final List<Bitmap> pieces = new ArrayList<>();

    final int count = bitmapPaint.size() > puzzleLayout.getAreaCount() ? puzzleLayout.getAreaCount()
        : bitmapPaint.size();

    for (int i = 0; i < count; i++) {
      final Target target = new Target() {
        @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
          pieces.add(bitmap);
          if (pieces.size() == count) {
            if (bitmapPaint.size() < puzzleLayout.getAreaCount()) {
              for (int i = 0; i < puzzleLayout.getAreaCount(); i++) {
                puzzleView.addPiece(pieces.get(i % count));
              }
            } else {
              puzzleView.addPieces(pieces);
            }
          }
          targets.remove(this);
        }

        @Override public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
      };

      //noinspection SuspiciousNameCombination
      Picasso.with(this)
          .load("file:///" + bitmapPaint.get(i))
          .resize(deviceWidth, deviceWidth)
          .centerInside()
          .config(Bitmap.Config.RGB_565)
          .into(target);

      targets.add(target);
    }
  }

  private void initView() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setNavigationOnClickListener(v -> onBackPressed());

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(view -> share());

    puzzleView = findViewById(R.id.puzzle_view);
    degreeSeekBar = findViewById(R.id.degree_seek_bar);

    puzzleView.setPuzzleLayout(puzzleLayout);
    puzzleView.setTouchEnable(true);
    puzzleView.setNeedDrawLine(false);
    puzzleView.setNeedDrawOuterLine(false);
    puzzleView.setLineSize(4);
    puzzleView.setLineColor(Color.BLACK);
    puzzleView.setSelectedLineColor(Color.BLACK);
    puzzleView.setHandleBarColor(Color.BLACK);
    puzzleView.setAnimateDuration(300);
    //puzzleView.setOnPieceSelectedListener(
    //    (piece, position) -> Snackbar.make(puzzleView, "Piece " + position + " selected",
    //        Snackbar.LENGTH_SHORT).show());

    // currently the SlantPuzzleLayout do not support padding
    puzzleView.setPiecePadding(10);

    ImageView btnReplace = findViewById(R.id.btn_replace);
    ImageView btnRotate = findViewById(R.id.btn_rotate);
    ImageView btnFlipHorizontal = findViewById(R.id.btn_flip_horizontal);
    ImageView btnFlipVertical = findViewById(R.id.btn_flip_vertical);
    ImageView btnBorder = findViewById(R.id.btn_border);
    ImageView btnCorner = findViewById(R.id.btn_corner);

    btnReplace.setOnClickListener(this);
    btnRotate.setOnClickListener(this);
    btnFlipHorizontal.setOnClickListener(this);
    btnFlipVertical.setOnClickListener(this);
    btnBorder.setOnClickListener(this);
    btnCorner.setOnClickListener(this);

    degreeSeekBar.setPointColor(Color.BLACK);
    degreeSeekBar.setCenterTextColor(Color.BLACK);
    degreeSeekBar.setTextColor(Color.BLACK);
    degreeSeekBar.setCurrentDegrees(puzzleView.getLineSize());
    degreeSeekBar.setDegreeRange(0, 30);
    degreeSeekBar.setScrollingListener(new DegreeSeekBar.ScrollingListener() {
      @Override public void onScrollStart() {

      }

      @Override public void onScroll(int currentDegrees) {
        switch (controlFlag) {
          case FLAG_CONTROL_LINE_SIZE:
            puzzleView.setLineSize(currentDegrees);
            break;
          case FLAG_CONTROL_CORNER:
            puzzleView.setPieceRadian(currentDegrees);
            break;
        }
      }

      @Override public void onScrollEnd() {

      }
    });
  }

  private void share() {
    // TODO
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_replace:
        showSelectedPhotoDialog();
        break;
      case R.id.btn_rotate:
        puzzleView.rotate(90f);
        break;
      case R.id.btn_flip_horizontal:
        puzzleView.flipHorizontally();
        break;
      case R.id.btn_flip_vertical:
        puzzleView.flipVertically();
        break;
      case R.id.btn_border:
        controlFlag = FLAG_CONTROL_LINE_SIZE;
        puzzleView.setNeedDrawLine(!puzzleView.isNeedDrawLine());
        if (puzzleView.isNeedDrawLine()) {
          degreeSeekBar.setVisibility(View.VISIBLE);
          degreeSeekBar.setCurrentDegrees(puzzleView.getLineSize());
          degreeSeekBar.setDegreeRange(0,30);
        } else {
          degreeSeekBar.setVisibility(View.INVISIBLE);
        }
        break;
      case R.id.btn_corner:
        if (controlFlag == FLAG_CONTROL_CORNER && degreeSeekBar.getVisibility() == View.VISIBLE){
          degreeSeekBar.setVisibility(View.INVISIBLE);
          return;
        }
        degreeSeekBar.setCurrentDegrees((int) puzzleView.getPieceRadian());
        controlFlag = FLAG_CONTROL_CORNER;
        degreeSeekBar.setVisibility(View.VISIBLE);
        degreeSeekBar.setDegreeRange(0,100);
        break;
    }
  }

  private void showSelectedPhotoDialog() {
    PhotoPicker.newInstance().setMaxCount(1).pick(this);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Define.DEFAULT_REQUEST_CODE && resultCode == RESULT_OK) {
      List<String> paths = data.getStringArrayListExtra(Define.PATHS);
      String path = paths.get(0);

      final Target target = new Target() {
        @Override public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
          puzzleView.replace(bitmap);
        }

        @Override public void onBitmapFailed(Drawable errorDrawable) {
          Snackbar.make(puzzleView, "Replace Failed!", Snackbar.LENGTH_SHORT).show();
        }

        @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
      };

      //noinspection SuspiciousNameCombination
      Picasso.with(this)
          .load("file:///" + path)
          .resize(deviceWidth, deviceWidth)
          .centerInside()
          .config(Bitmap.Config.RGB_565)
          .into(target);
    }
  }
}
