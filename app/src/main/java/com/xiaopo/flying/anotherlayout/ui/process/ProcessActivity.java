package com.xiaopo.flying.anotherlayout.ui.process;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.Colors;
import com.xiaopo.flying.anotherlayout.kits.PuzzleKit;
import com.xiaopo.flying.anotherlayout.model.ColorItem;
import com.xiaopo.flying.anotherlayout.model.HandleItem;
import com.xiaopo.flying.anotherlayout.model.data.Stores;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.ColorItemBinder;
import com.xiaopo.flying.anotherlayout.ui.widget.HandleContainer;
import com.xiaopo.flying.pixelcrop.DegreeSeekBar;
import com.xiaopo.flying.poiphoto.Define;
import com.xiaopo.flying.poiphoto.PhotoPicker;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.PuzzleView;
import java.util.ArrayList;
import java.util.List;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wupanjie
 */
public class ProcessActivity extends AppCompatActivity {
  public static final String INTENT_KEY_PATHS = "photo_path";
  public static final String INTENT_KEY_SIZE = "piece_size";
  public static final String INTENT_KEY_THEME = "theme_id";
  public static final String INTENT_KEY_TYPE = "piece_type";

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.puzzle_view) PuzzleView puzzleView;
  @BindView(R.id.handle_container) HandleContainer handleContainer;

  private PuzzleLayout puzzleLayout;
  private List<String> bitmapPaint;

  private List<Target> targets = new ArrayList<>();
  private int deviceSize = 0;
  private List<HandleItem> handleItems = new ArrayList<>(5);

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_process);
    ButterKnife.bind(this);

    deviceSize = getResources().getDisplayMetrics().widthPixels;

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
          .resize(deviceSize, deviceSize)
          .centerInside()
          .config(Bitmap.Config.RGB_565)
          .into(target);

      targets.add(target);
    }
  }

  private void initView() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setNavigationOnClickListener(v -> onBackPressed());
    toolbar.inflateMenu(R.menu.menu_process);
    toolbar.setOnMenuItemClickListener(item -> {
      switch (item.getItemId()){
        case R.id.action_save_layout:
          PuzzleLayout.Info info = puzzleLayout.generateInfo();
          Stores.instance(this).saveLayout(info);
          break;
      }
      return true;
    });

    ViewGroup.LayoutParams params = puzzleView.getLayoutParams();
    params.width = deviceSize;
    params.height = deviceSize;
    puzzleView.setLayoutParams(params);
    puzzleView.setPuzzleLayout(puzzleLayout);
    puzzleView.setTouchEnable(true);

    addHandleItems();

    handleContainer.setHandleItems(handleItems);
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
      Picasso.with(this).load("file:///" + path).resize(deviceSize, deviceSize)
          .centerInside()
          .config(Bitmap.Config.RGB_565)
          .into(target);
    }
  }

  private void addHandleItems() {
    handleItems.clear();

    HandleItem color = new HandleItem(R.drawable.ic_palette_black_24dp, colorView());
    handleItems.add(color);

    HandleItem ratio = new HandleItem(R.drawable.ic_image_aspect_ratio_black_24dp, ratioView());
    handleItems.add(ratio);

    HandleItem transform = new HandleItem(R.drawable.ic_transform_black_24dp, transformView());
    handleItems.add(transform);

    HandleItem border = new HandleItem(R.drawable.ic_border_style_black_24dp, borderView());
    handleItems.add(border);

    HandleItem round = new HandleItem(R.drawable.ic_rounded_corner_black_24dp, roundView());
    handleItems.add(round);
  }

  // TODO Handle detail view
  private View roundView() {
    View roundView = LayoutInflater.from(this).inflate(R.layout.handle_item_round, null);
    DegreeSeekBar seekBar = roundView.findViewById(R.id.seek_bar);
    seekBar.setDegreeRange(0, 100);
    seekBar.setCurrentDegrees((int) puzzleView.getPieceRadian());
    seekBar.setScrollingListener(new DegreeSeekBar.SimpleScrollingListener() {
      @Override public void onScroll(int currentDegrees) {
        puzzleView.setPieceRadian(currentDegrees);
      }
    });
    return roundView;
  }

  private View borderView() {
    View borderView = LayoutInflater.from(this).inflate(R.layout.handle_item_round, null);
    DegreeSeekBar seekBar = borderView.findViewById(R.id.seek_bar);
    seekBar.setDegreeRange(0, 30);
    seekBar.setCurrentDegrees((int) puzzleView.getPiecePadding());
    seekBar.setScrollingListener(new DegreeSeekBar.SimpleScrollingListener() {
      @Override public void onScroll(int currentDegrees) {
        puzzleView.setPiecePadding(currentDegrees);
      }
    });
    return borderView;
  }

  private View transformView() {
    View transformView = LayoutInflater.from(this).inflate(R.layout.handle_item_transform, null);
    View flipHorizontal = transformView.findViewById(R.id.btn_flip_horizontal);
    View flipVertical = transformView.findViewById(R.id.btn_flip_vertical);
    View rotateLeft = transformView.findViewById(R.id.btn_rotate_left);
    View rotateRight = transformView.findViewById(R.id.btn_rotate_right);

    View.OnClickListener listener = view -> {
      switch (view.getId()) {
        case R.id.btn_flip_horizontal:
          puzzleView.flipHorizontally();
          break;
        case R.id.btn_flip_vertical:
          puzzleView.flipVertically();
          break;
        case R.id.btn_rotate_left:
          puzzleView.rotate(-90f);
          break;
        case R.id.btn_rotate_right:
          puzzleView.rotate(90f);
          break;
      }
    };

    flipVertical.setOnClickListener(listener);
    flipHorizontal.setOnClickListener(listener);
    rotateLeft.setOnClickListener(listener);
    rotateRight.setOnClickListener(listener);
    return transformView;
  }

  private View ratioView() {
    View ratioView = LayoutInflater.from(this).inflate(R.layout.handle_item_crop, null);
    View ratio_1_1 = ratioView.findViewById(R.id.btn_crop_square);
    View ratio_4_3 = ratioView.findViewById(R.id.btn_crop_4_3);
    View ratio_16_9 = ratioView.findViewById(R.id.btn_crop_16_9);

    ViewGroup.LayoutParams params = puzzleView.getLayoutParams();
    View.OnClickListener listener = view -> {
      switch (view.getId()) {
        case R.id.btn_crop_square:
          params.width = deviceSize;
          params.height = deviceSize;
          break;
        case R.id.btn_crop_4_3:
          params.width = deviceSize;
          params.height = deviceSize / 4 * 3;
          break;
        case R.id.btn_crop_16_9:
          params.width = deviceSize;
          params.height = deviceSize / 16 * 9;
          break;
      }

      puzzleView.setNeedResetPieceMatrix(false);
      puzzleView.setLayoutParams(params);
    };

    ratio_1_1.setOnClickListener(listener);
    ratio_4_3.setOnClickListener(listener);
    ratio_16_9.setOnClickListener(listener);
    return ratioView;
  }

  private View colorView() {
    RecyclerView colorView =
        (RecyclerView) LayoutInflater.from(this).inflate(R.layout.handle_item_color, null);
    List<ColorItem> allColors = Colors.all();
    allColors.get(0).setSelected(true);
    MultiTypeAdapter adapter = new MultiTypeAdapter(allColors);
    ColorItemBinder colorItemBinder = new ColorItemBinder(colorView, allColors);
    colorItemBinder.setOnColorSelectedListener(color -> puzzleView.setBackgroundColor(color));
    adapter.register(ColorItem.class, colorItemBinder);
    colorView.setAdapter(adapter);
    colorView.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    return colorView;
  }
}
