package com.xiaopo.flying.anotherlayout.ui.process;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.PuzzleKit;
import com.xiaopo.flying.anotherlayout.model.ColorItem;
import com.xiaopo.flying.anotherlayout.model.HandleItem;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.ColorItemBinder;
import com.xiaopo.flying.anotherlayout.ui.widget.HandleContainer;
import com.xiaopo.flying.poiphoto.Define;
import com.xiaopo.flying.poiphoto.PhotoPicker;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.SquarePuzzleView;
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
  @BindView(R.id.puzzle_view) SquarePuzzleView puzzleView;
  @BindView(R.id.handle_container) HandleContainer handleContainer;

  private PuzzleLayout puzzleLayout;
  private List<String> bitmapPaint;

  private List<Target> targets = new ArrayList<>();
  private int deviceWidth = 0;
  private List<HandleItem> handleItems = new ArrayList<>(5);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_process);
    ButterKnife.bind(this);

    deviceWidth = getResources().getDisplayMetrics().widthPixels;

    int type = getIntent().getIntExtra(INTENT_KEY_TYPE, 0);
    int pieceSize = getIntent().getIntExtra(INTENT_KEY_SIZE, 0);
    int themeId = getIntent().getIntExtra(INTENT_KEY_THEME, 0);
    bitmapPaint = getIntent().getStringArrayListExtra(INTENT_KEY_PATHS);
    puzzleLayout = PuzzleKit.getPuzzleLayout(type, pieceSize, themeId);

    initView();

    puzzleView.post(this::loadPhoto);
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  private void loadPhoto() {
    final List<Bitmap> pieces = new ArrayList<>();

    final int count = bitmapPaint.size() > puzzleLayout.getAreaCount() ? puzzleLayout.getAreaCount()
        : bitmapPaint.size();

    for (int i = 0; i < count; i++) {
      final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
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

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

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

    puzzleView.setPuzzleLayout(puzzleLayout);
    puzzleView.setTouchEnable(true);
    puzzleView.setPiecePadding(20);

    addHandleItems();

    handleContainer.setHandleItems(handleItems);
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
          puzzleView.replace(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
          Snackbar.make(puzzleView, "Replace Failed!", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

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

  private void addHandleItems() {
    handleItems.clear();

    RecyclerView colorView =
        (RecyclerView) LayoutInflater.from(this).inflate(R.layout.handle_item_color, null);
    List<ColorItem> allColors = fetchColors();
    MultiTypeAdapter adapter = new MultiTypeAdapter(allColors);
    adapter.register(ColorItem.class, new ColorItemBinder(colorView, allColors));
    colorView.setAdapter(adapter);
    colorView.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    HandleItem color = new HandleItem(R.drawable.ic_palette_black_24dp);
    color.setHandleView(colorView);
    handleItems.add(color);

    HandleItem ratio = new HandleItem(R.drawable.ic_image_aspect_ratio_black_24dp);
    handleItems.add(ratio);

    HandleItem transform = new HandleItem(R.drawable.ic_transform_black_24dp);
    handleItems.add(transform);

    HandleItem border = new HandleItem(R.drawable.ic_border_style_black_24dp);
    handleItems.add(border);

    HandleItem round = new HandleItem(R.drawable.ic_rounded_corner_black_24dp);
    handleItems.add(round);
  }

  private List<ColorItem> fetchColors() {
    List<ColorItem> colorItems = new ArrayList<>();
    colorItems.add(new ColorItem(Color.WHITE));
    colorItems.add(new ColorItem(Color.RED));
    colorItems.add(new ColorItem(Color.BLUE));
    colorItems.add(new ColorItem(Color.GRAY));
    colorItems.add(new ColorItem(Color.GREEN));
    colorItems.add(new ColorItem(Color.CYAN));
    colorItems.add(new ColorItem(Color.DKGRAY));
    colorItems.add(new ColorItem(Color.LTGRAY));
    colorItems.add(new ColorItem(Color.MAGENTA));
    colorItems.add(new ColorItem(Color.YELLOW));
    return colorItems;
  }
}
