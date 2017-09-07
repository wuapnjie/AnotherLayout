package com.xiaopo.flying.anotherlayout.ui.page.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.Colors;
import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.anotherlayout.kits.Toasts;
import com.xiaopo.flying.anotherlayout.model.ColorItem;
import com.xiaopo.flying.anotherlayout.model.HandleItem;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.ColorItemBinder;
import com.xiaopo.flying.anotherlayout.ui.widget.HandleContainer;
import com.xiaopo.flying.anotherlayout.ui.widget.PhotoPuzzleView;
import com.xiaopo.flying.pixelcrop.DegreeSeekBar;
import com.xiaopo.flying.puzzle.PuzzleLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wupanjie
 */
public class ProcessUI implements IProcessUI, Toolbar.OnMenuItemClickListener {
  private static final String TAG = "ProcessUI";
  private final View contentRootView;
  private final ProcessController controller;

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.puzzle_view)
  PhotoPuzzleView puzzleView;
  @BindView(R.id.handle_container)
  HandleContainer handleContainer;

  private PuzzleLayout puzzleLayout;
  private List<HandleItem> handleItems = new ArrayList<>(5);
  private int deviceSize = 0;

  ProcessUI(ProcessController controller, View contentRootView) {
    this.controller = controller;
    this.contentRootView = contentRootView;
    deviceSize = DipPixelKit.getDeviceWidth(controller.context());

    ButterKnife.bind(this, contentRootView);
  }

  @Override
  public void initUI() {
    toolbar.setNavigationOnClickListener(v -> controller.onBackPressed());
    toolbar.inflateMenu(R.menu.menu_process);
    toolbar.setOnMenuItemClickListener(this);

    ViewGroup.LayoutParams params = puzzleView.getLayoutParams();
    params.width = deviceSize;
    params.height = deviceSize;
    puzzleView.setLayoutParams(params);
    puzzleView.setTouchEnable(true);
    puzzleView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

    addHandleItems();

    handleContainer.setHandleItems(handleItems);
  }

  @Override
  public void onDestroy() {
  }


  @Override
  public void setPuzzleLayout(PuzzleLayout puzzleLayout) {
    this.puzzleLayout = puzzleLayout;
    puzzleView.setPuzzleLayout(puzzleLayout);
  }

  @Override
  public PuzzleLayout setPuzzleLayoutInfo(PuzzleLayout.Info puzzleLayoutInfo) {
    ViewGroup.LayoutParams layoutParams = puzzleView.getLayoutParams();
    layoutParams.width = deviceSize;
    layoutParams.height = (int) (deviceSize / puzzleLayoutInfo.width() * puzzleLayoutInfo.height());
    puzzleView.setLayoutParams(layoutParams);

    puzzleView.setPuzzleLayout(puzzleLayoutInfo);
    puzzleView.setNeedResetPieceMatrix(false);
    this.puzzleLayout = puzzleView.getPuzzleLayout();

    return this.puzzleLayout;
  }

  @Override
  public void addPiece(Bitmap piece, String path) {
    puzzleView.addPiece(piece, path);
  }

  @Override
  public void addPiece(Bitmap piece, String path, Matrix initialMatrix) {
    puzzleView.addPiece(piece, path, initialMatrix);
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

  private View roundView() {
    View roundView = LayoutInflater.from(controller.context())
        .inflate(R.layout.handle_item_round, handleContainer, false);
    DegreeSeekBar seekBar = roundView.findViewById(R.id.seek_bar);
    seekBar.setDegreeRange(0, 100);
    seekBar.setCurrentDegrees((int) puzzleView.getPieceRadian());
    seekBar.setScrollingListener(new DegreeSeekBar.SimpleScrollingListener() {
      @Override
      public void onScroll(int currentDegrees) {
        puzzleView.setPieceRadian(currentDegrees);
      }
    });
    return roundView;
  }

  private View borderView() {
    View borderView = LayoutInflater.from(controller.context())
        .inflate(R.layout.handle_item_round, handleContainer, false);
    DegreeSeekBar seekBar = borderView.findViewById(R.id.seek_bar);
    seekBar.setDegreeRange(0, 30);
    seekBar.setCurrentDegrees((int) puzzleView.getPiecePadding());
    seekBar.setScrollingListener(new DegreeSeekBar.SimpleScrollingListener() {
      @Override
      public void onScroll(int currentDegrees) {
        puzzleView.setPiecePadding(currentDegrees);
      }
    });
    return borderView;
  }

  private View transformView() {
    View transformView = LayoutInflater.from(controller.context())
        .inflate(R.layout.handle_item_transform, handleContainer, false);
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
    View ratioView = LayoutInflater.from(controller.context())
        .inflate(R.layout.handle_item_crop, handleContainer, false);
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
        (RecyclerView) LayoutInflater.from(controller.context())
            .inflate(R.layout.handle_item_color, handleContainer, false);
    List<ColorItem> allColors = Colors.all();
    allColors.get(0).setSelected(true);
    MultiTypeAdapter adapter = new MultiTypeAdapter(allColors);
    ColorItemBinder colorItemBinder = new ColorItemBinder(colorView, allColors);
    colorItemBinder.setOnColorSelectedListener(color -> puzzleView.setBackgroundColor(color));
    adapter.register(ColorItem.class, colorItemBinder);
    colorView.setAdapter(adapter);
    colorView.setLayoutManager(
        new LinearLayoutManager(controller.context(), LinearLayoutManager.HORIZONTAL, false));

    return colorView;
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_save_image:
        controller.saveImage(puzzleLayout.generateInfo(), puzzleView.generatePieceInfo());
        break;
      case R.id.action_save_layout:
        PuzzleLayout.Info info = puzzleLayout.generateInfo();
        controller.saveLayout(info);
        break;
    }
    return true;
  }

  @Override
  public void showSaveSuccess() {
    Toasts.show(controller.context(), R.string.save_layout_success);
  }

  @Override
  public Bitmap createBitmap() {
    Bitmap bitmap = Bitmap.createBitmap(puzzleView.getWidth(), puzzleView.getHeight(), Bitmap.Config.RGB_565);
    Canvas canvas = new Canvas(bitmap);
    puzzleView.clearHandlingPieces();
    puzzleView.draw(canvas);
    return bitmap;
  }
}
