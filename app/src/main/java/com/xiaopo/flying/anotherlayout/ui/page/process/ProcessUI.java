package com.xiaopo.flying.anotherlayout.ui.page.process;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.Colors;
import com.xiaopo.flying.anotherlayout.kits.DebouncedOnClickListener;
import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.anotherlayout.kits.Toasts;
import com.xiaopo.flying.anotherlayout.kits.guava.Optional;
import com.xiaopo.flying.anotherlayout.kits.imageload.PhotoManager;
import com.xiaopo.flying.anotherlayout.model.ColorItem;
import com.xiaopo.flying.anotherlayout.model.HandleItem;
import com.xiaopo.flying.anotherlayout.model.Photo;
import com.xiaopo.flying.anotherlayout.ui.PlaceHolderDrawable;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.ColorItemBinder;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.PhotoBinder;
import com.xiaopo.flying.anotherlayout.ui.widget.HandleContainer;
import com.xiaopo.flying.anotherlayout.ui.widget.PhotoPuzzleView;
import com.xiaopo.flying.pixelcrop.DegreeSeekBar;
import com.xiaopo.flying.puzzle.PuzzleLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wupanjie
 */
public class ProcessUI implements IProcessUI, PopupMenu.OnMenuItemClickListener {
  public static final int PROCESS_MODE_COMMON = 0;
  public static final int PROCESS_MODE_STYLE = 1;
  public static final int PROCESS_MODE_LAYOUT = 2;

  private static final int UI_MODE_EDIT = 1001;
  private static final int UI_MODE_REPLACE = 1002;

  private static final int ANIMATE_DURATION = 500;

  private final View contentRootView;
  private final ProcessController controller;

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.puzzle_view) PhotoPuzzleView puzzleView;
  @BindView(R.id.handle_container) HandleContainer handleContainer;
  @BindView(R.id.photo_list) RecyclerView photoList;
  @BindView(R.id.icon_more) TextView iconMore;
  @BindView(R.id.fake_view) View fakeView;
  @BindView(R.id.btn_menu) View btnMenu;

  private PuzzleLayout puzzleLayout;
  private Optional<PuzzleLayout.Info> puzzleLayoutInfo = Optional.absent();
  private List<HandleItem> handleItems = new ArrayList<>(5);
  private int deviceSize = 0;
  @ProcessMode private int processMode;
  private int uiMode = UI_MODE_EDIT;

  private MultiTypeAdapter photoAdapter;
  private Items photos = new Items();
  private int photoHeight;

  private final Set<Integer> selectedPositions = new TreeSet<>();

  ProcessUI(ProcessController controller, ViewGroup contentRootView, @ProcessMode int processMode) {
    this.controller = controller;
    this.contentRootView = contentRootView;
    deviceSize = DipPixelKit.getDeviceWidth(controller.context());
    this.processMode = processMode;

    ButterKnife.bind(this, contentRootView);

  }

  @Override
  public void initUI() {
    toolbar.setNavigationOnClickListener(v -> controller.onBackPressed());

    puzzleView.setTouchEnable(true);
    puzzleView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

    addHandleItems();
    handleContainer.setHandleItems(handleItems);
    if (processMode == PROCESS_MODE_LAYOUT) {
      handleContainer.setVisibility(View.INVISIBLE);
    }

    final Context context = controller.context();

    photoAdapter = new MultiTypeAdapter();
    photoHeight = context.getResources().getDimensionPixelSize(R.dimen
        .ratio_photo_height);
    photoAdapter.register(Photo.class, new PhotoBinder(selectedPositions, 9, -1, photoHeight));
    photoList.setAdapter(photoAdapter);
    photoList.setLayoutManager(
        new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

    btnMenu.setOnClickListener(new DebouncedOnClickListener() {
      @Override public void doClick(View view) {
        if (uiMode == UI_MODE_EDIT) {
          showPopupMenu();
        } else if (uiMode == UI_MODE_REPLACE) {
          processMode = PROCESS_MODE_COMMON;
          changeToEditScene(ANIMATE_DURATION);
        }
      }
    });

    PhotoManager photoManager = new PhotoManager(context);
    photoManager.fetchAllPhotos()
        .compose(controller.bindToLifecycle())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(photoList -> {
          photos.addAll(photoList);
          photoAdapter.setItems(photos);
          photoAdapter.notifyDataSetChanged();
        });
  }

  private void showPopupMenu() {
    PopupMenu popupMenu = new PopupMenu(controller.context(), fakeView);
    popupMenu.inflate(R.menu.menu_process);
    popupMenu.setOnMenuItemClickListener(this);
    popupMenu.show();
  }

  @Override
  public void onDestroy() {
  }

  @Override public boolean onBackPressed() {
    if (uiMode == UI_MODE_REPLACE && processMode != PROCESS_MODE_LAYOUT) {
      changeToEditScene(ANIMATE_DURATION);
      return true;
    }
    return false;
  }


  @Override
  public void setPuzzleLayout(@NonNull PuzzleLayout puzzleLayout) {
    this.puzzleLayout = puzzleLayout;

    ViewGroup.LayoutParams params = puzzleView.getLayoutParams();
    params.width = deviceSize;
    params.height = deviceSize;
    puzzleView.setLayoutParams(params);

    puzzleView.setPuzzleLayout(puzzleLayout);
    puzzleView.setBackgroundColor(puzzleLayout.getColor());
  }

  @Override
  public PuzzleLayout setPuzzleLayoutInfo(@NonNull PuzzleLayout.Info puzzleLayoutInfo) {
    this.puzzleLayoutInfo = Optional.of(puzzleLayoutInfo);

    ViewGroup.LayoutParams layoutParams = puzzleView.getLayoutParams();
    layoutParams.width = deviceSize;
    layoutParams.height = (int) (deviceSize / puzzleLayoutInfo.width() * puzzleLayoutInfo.height());
    puzzleView.setLayoutParams(layoutParams);

    puzzleView.setPuzzleLayout(puzzleLayoutInfo);
    puzzleView.setNeedResetPieceMatrix(false);
    this.puzzleLayout = puzzleView.getPuzzleLayout();

    if (processMode == PROCESS_MODE_LAYOUT) {
      puzzleView.setLineSize(8);
      changeToReplaceScene(0);
    }

    return this.puzzleLayout;
  }

  @Override
  public void addPiece(final Bitmap piece, final String path) {
    puzzleView.post(() -> puzzleView.addPiece(piece, path));
  }

  @Override
  public void addPiece(final Bitmap piece, final String path, final Matrix initialMatrix) {
    puzzleView.post(() -> puzzleView.addPiece(piece, path, initialMatrix));
  }

  @Override public void showPlaceholder() {
    if (puzzleLayoutInfo.get().padding == 0) {
      puzzleView.setNeedDrawLine(true);
    } else {
      puzzleView.setNeedDrawLine(false);
    }

    for (int i = 0; i < puzzleView.getPuzzleLayout().getAreaCount(); i++) {
      puzzleView.addPiece(PlaceHolderDrawable.instance);
    }
  }

  private void addHandleItems() {
    handleItems.clear();

    HandleItem color = new HandleItem(R.drawable.ic_palette_black_24dp, colorView());
    handleItems.add(color);

    if (processMode == PROCESS_MODE_COMMON) {
      HandleItem ratio = new HandleItem(R.drawable.ic_image_aspect_ratio_black_24dp, ratioView());
      handleItems.add(ratio);
    }

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
    if (puzzleLayoutInfo.isPresent()) {
      seekBar.setCurrentDegrees((int) puzzleLayoutInfo.get().radian);
    } else {
      seekBar.setCurrentDegrees((int) puzzleView.getPieceRadian());
    }
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
      case R.id.action_replace_photo:
        changeToReplaceScene(ANIMATE_DURATION);
        break;
      case R.id.action_save_image:
        controller.saveImage(puzzleLayout.generateInfo(), puzzleView.generatePieceInfo());
        break;
      case R.id.action_save_layout:
        PuzzleLayout.Info info = puzzleLayout.generateInfo();
        controller.saveLayout(info);
        break;
      case R.id.action_layout_done:
        // TODO
        Toasts.show(controller.context(), "DONE");
        break;
    }
    return true;
  }

  private void changeToReplaceScene(final int duration) {
    if (uiMode == UI_MODE_REPLACE) return;
    uiMode = UI_MODE_REPLACE;

    iconMore.setText(R.string.action_layout_done);
    iconMore.setBackground(null);

    handleContainer.setVisibility(View.INVISIBLE);

    puzzleView.animate()
        .translationY(-200)
        .scaleX(0.8f)
        .scaleY(0.8f)
        .setDuration(duration)
        .start();

    photoList.setVisibility(View.VISIBLE);
    photoList.setAlpha(0f);
    photoList.setTranslationY(photoHeight);
    photoList.animate()
        .alpha(1f)
        .translationY(0f)
        .setListener(null)
        .setDuration(duration)
        .start();
  }

  private void changeToEditScene(final int duration) {
    if (uiMode == UI_MODE_EDIT) return;
    uiMode = UI_MODE_EDIT;

    iconMore.setText("");
    iconMore.setBackgroundResource(R.drawable.ic_more_vert_black_24dp);

    puzzleView.animate()
        .translationY(0f)
        .scaleX(1f)
        .scaleY(1f)
        .setDuration(duration)
        .start();

    photoList.animate()
        .alpha(1f)
        .translationY(photoHeight)
        .setDuration(duration)
        .setListener(new AnimatorListenerAdapter() {
          @Override public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            photoList.setVisibility(View.INVISIBLE);
            handleContainer.setVisibility(View.VISIBLE);

            handleContainer.setAlpha(0f);
            handleContainer.animate()
                .alpha(1f)
                .setDuration(300)
                .start();
          }
        })
        .start();


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

  @IntDef({PROCESS_MODE_COMMON, PROCESS_MODE_STYLE, PROCESS_MODE_LAYOUT})
  @Retention(RetentionPolicy.SOURCE) @interface ProcessMode {

  }
}
