package com.xiaopo.flying.puzzzzle.ui.select;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.xiaopo.flying.poiphoto.PhotoManager;
import com.xiaopo.flying.poiphoto.datatype.Album;
import com.xiaopo.flying.poiphoto.datatype.Photo;
import com.xiaopo.flying.puzzle.slant.SlantPuzzleLayout;
import com.xiaopo.flying.puzzzzle.R;
import com.xiaopo.flying.puzzzzle.kits.SingleIOSwitch;
import com.xiaopo.flying.puzzzzle.model.PhotoHeader;
import com.xiaopo.flying.puzzzzle.ui.adapter.AlbumTitleBinder;
import com.xiaopo.flying.puzzzzle.ui.adapter.PhotoBinder;
import com.xiaopo.flying.puzzzzle.ui.adapter.PuzzleAdapter;
import com.xiaopo.flying.puzzzzle.ui.process.ProcessActivity;
import com.xiaopo.flying.puzzzzle.ui.adapter.AlbumItemDecoration;
import com.xiaopo.flying.puzzzzle.ui.adapter.PhotoHeaderBinder;
import com.xiaopo.flying.puzzzzle.ui.adapter.PhotoItemDecoration;
import com.xiaopo.flying.puzzzzle.ui.widget.BlurView;
import com.xiaopo.flying.puzzzzle.kits.DipPixelKit;
import com.xiaopo.flying.puzzzzle.kits.PuzzleKit;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.getSimpleName();
  public static final int CODE_REQUEST_PERMISSION = 110;

  @BindView(R.id.puzzle_list) RecyclerView puzzleList;
  RecyclerView photoList;
  RecyclerView albumList;
  @BindView(R.id.tab_layout) TabLayout tabLayout;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.blur_view) BlurView blurView;
  @BindView(R.id.viewPager) ViewPager viewPager;
  @BindView(R.id.content_main) FrameLayout contentMain;

  private PuzzleAdapter puzzleAdapter;

  private MultiTypeAdapter photoAdapter;
  private MultiTypeAdapter albumAdapter;

  private ArrayMap<String, Bitmap> arrayBitmap = new ArrayMap<>();
  private ArrayList<String> selectedPath = new ArrayList<>();
  private List<Bitmap> bitmaps = new ArrayList<>();
  private List<Target> targets = new ArrayList<>();

  private Items allPhoto = new Items();
  private Items albumPhoto = new Items();

  private int deviceWidth;

  private GridLayoutManager albumPhotoLayoutManager;

  private PuzzleHandler puzzleHandler;

  private Set<Integer> selectedPositions = new TreeSet<>();

  private CompositeDisposable compositeDisposables = new CompositeDisposable();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    puzzleHandler = new PuzzleHandler(this);

    initView();

    deviceWidth = DipPixelKit.getDeviceWidth(this);

    AndPermission.with(this)
        .requestCode(CODE_REQUEST_PERMISSION)
        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        .callback(this)
        .start();
  }

  @PermissionYes(CODE_REQUEST_PERMISSION)
  private void getPermissionYes(List<String> grantedPermissions) {
    loadPhoto();
  }

  @PermissionNo(CODE_REQUEST_PERMISSION)
  private void getPermissionNo(List<String> deniedPermissions) {
    Toast.makeText(this, "必须要权限", Toast.LENGTH_SHORT).show();
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 110
        && grantResults[0] == PackageManager.PERMISSION_GRANTED
        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
      loadPhoto();
    }
  }

  private void initView() {
    //about puzzle list
    puzzleAdapter = new PuzzleAdapter();
    puzzleList.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    puzzleList.setHasFixedSize(true);
    puzzleList.setAdapter(puzzleAdapter);
    puzzleAdapter.setOnItemClickListener((puzzleLayout, themeId) -> {
      Intent intent = new Intent(MainActivity.this, ProcessActivity.class);
      if (puzzleLayout instanceof SlantPuzzleLayout) {
        intent.putExtra(ProcessActivity.INTENT_KEY_TYPE, 0);
      } else {
        intent.putExtra(ProcessActivity.INTENT_KEY_TYPE, 1);
      }
      intent.putStringArrayListExtra(ProcessActivity.INTENT_KEY_PATHS, selectedPath);
      intent.putExtra(ProcessActivity.INTENT_KEY_SIZE, selectedPath.size());
      intent.putExtra(ProcessActivity.INTENT_KEY_THEME, themeId);

      startActivity(intent);
    });

    //about photo list
    photoList = new RecyclerView(this);
    photoAdapter = new MultiTypeAdapter();
    PhotoBinder photoBinder = new PhotoBinder(selectedPositions);
    photoBinder.setOnPhotoSelectedListener((photo, position) -> {
      int pos = albumPhoto.indexOf(photo);
      albumAdapter.notifyItemChanged(pos);
      selectPhoto(photo);
    });
    photoAdapter.register(PhotoHeader.class, new PhotoHeaderBinder());
    photoAdapter.register(Photo.class, photoBinder);
    GridLayoutManager allPhotoLayoutManager = new GridLayoutManager(this, 4);
    allPhotoLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        return position == 0 ? 4 : 1;
      }
    });
    photoList.setLayoutManager(allPhotoLayoutManager);
    photoList.setAdapter(photoAdapter);

    albumList = new RecyclerView(this);
    albumAdapter = new MultiTypeAdapter();
    PhotoBinder albumPhotoBinder = new PhotoBinder(selectedPositions);
    albumPhotoBinder.setOnPhotoSelectedListener((photo, position) -> {
      int pos = allPhoto.indexOf(photo);
      photoAdapter.notifyItemChanged(pos);
      selectPhoto(photo);
    });
    albumAdapter.register(Album.class, new AlbumTitleBinder());
    albumAdapter.register(Photo.class, albumPhotoBinder);
    albumAdapter.register(PhotoHeader.class, new PhotoHeaderBinder());

    albumPhotoLayoutManager = new GridLayoutManager(MainActivity.this, 4);
    albumPhotoLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        if (position == 0) return 4;
        return albumPhoto.get(position) instanceof Album ? 4 : 1;
      }
    });
    albumList.setLayoutManager(albumPhotoLayoutManager);
    albumList.setAdapter(albumAdapter);

    List<View> viewList = Arrays.asList(photoList, albumList);
    List<String> titleList =
        Arrays.asList(getString(R.string.all_photo), getString(R.string.album));
    viewPager.setAdapter(new SelectPagerAdapter(viewList, titleList));

    //about others
    tabLayout.setupWithViewPager(viewPager);

    blurView.setBlurredView(puzzleList);
    puzzleList.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        blurView.invalidate();
      }
    });
  }

  public void fetchBitmap(final String path) {
    Log.d(TAG, "fetchBitmap: ");
    final Target target = new Target() {
      @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        Log.d(TAG, "onBitmapLoaded: ");
        arrayBitmap.put(path, bitmap);
        bitmaps.add(bitmap);
        selectedPath.add(path);

        refreshLayout();
        targets.remove(this);
      }

      @Override public void onBitmapFailed(Drawable errorDrawable) {

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

    targets.add(target);
  }

  private void refreshLayout() {
    puzzleList.post(
        () -> puzzleAdapter.refreshData(PuzzleKit.getPuzzleLayouts(bitmaps.size()), bitmaps));
  }

  private void selectPhoto(Photo photo) {
    if (photo.isSelected()) {
      Message message = Message.obtain();
      message.what = 120;
      message.obj = photo.getPath();
      puzzleHandler.sendMessage(message);

      //prefetch the photo
      //noinspection SuspiciousNameCombination
      Picasso.with(MainActivity.this)
          .load("file:///" + photo.getPath())
          .resize(deviceWidth, deviceWidth)
          .centerInside()
          .memoryPolicy(MemoryPolicy.NO_CACHE)
          .fetch();
    } else {
      Bitmap bitmap = arrayBitmap.remove(photo.getPath());
      bitmaps.remove(bitmap);
      selectedPath.remove(photo.getPath());

      puzzleAdapter.refreshData(PuzzleKit.getPuzzleLayouts(bitmaps.size()), bitmaps);
    }
  }

  private void loadPhoto() {
    Disposable disposable = Single.just(new PhotoManager(this))
        .map(PhotoManager::getAllPhoto)
        .compose(new SingleIOSwitch<>())
        .subscribe(photos -> {
          allPhoto.clear();
          allPhoto.add(new PhotoHeader());
          allPhoto.addAll(photos);
          ArrayMap<String, List<Photo>> albumArray = new ArrayMap<>();
          albumPhoto.add(new PhotoHeader());
          for (Photo photo : photos) {
            if (albumArray.containsKey(photo.getBucketId())) {
              albumArray.get(photo.getBucketId()).add(photo);
            } else {
              List<Photo> list = new ArrayList<>();
              list.add(photo);
              albumArray.put(photo.getBucketId(), list);
            }
          }
          for (String key : albumArray.keySet()) {
            List<Photo> list = albumArray.get(key);
            Album album = new Album();
            album.setId(key);
            album.setName(list.get(0).getBuckedName());
            albumPhoto.add(album);
            albumPhoto.addAll(list);
          }
          final int space = DipPixelKit.dip2px(MainActivity.this, 2);
          AlbumItemDecoration albumItemDecoration = new AlbumItemDecoration(albumPhoto, space,
              albumPhotoLayoutManager.getSpanSizeLookup());
          albumAdapter.setItems(albumPhoto);
          albumAdapter.notifyDataSetChanged();
          albumList.addItemDecoration(albumItemDecoration);

          PhotoItemDecoration photoItemDecoration = new PhotoItemDecoration(4, space, false);
          photoAdapter.setItems(allPhoto);
          photoAdapter.notifyDataSetChanged();
          photoList.addItemDecoration(photoItemDecoration);
        });
    compositeDisposables.add(disposable);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    compositeDisposables.clear();
  }

  private static class PuzzleHandler extends Handler {
    private WeakReference<MainActivity> mReference;

    PuzzleHandler(MainActivity activity) {
      mReference = new WeakReference<>(activity);
    }

    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (msg.what == 119) {
        mReference.get().refreshLayout();
      } else if (msg.what == 120) {
        mReference.get().fetchBitmap((String) msg.obj);
      }
    }
  }

  private class SelectPagerAdapter extends PagerAdapter {

    private final List<View> viewList;
    private final List<String> titleList;

    private SelectPagerAdapter(List<View> viewList, List<String> titleList) {
      this.viewList = viewList;
      this.titleList = titleList;
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
      container.addView(viewList.get(position));
      return viewList.get(position);
    }

    @Override public int getCount() {
      return viewList.size();
    }

    @Override public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
      super.destroyItem(container, position, object);
      container.removeView((View) object);
    }

    @Override public CharSequence getPageTitle(int position) {
      return titleList.get(position);
    }
  }
}
