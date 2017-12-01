package com.xiaopo.flying.anotherlayout.ui.page.select;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.anotherlayout.kits.PuzzleKit;
import com.xiaopo.flying.anotherlayout.kits.WeakHandler;
import com.xiaopo.flying.anotherlayout.kits.imageload.ImageEngine;
import com.xiaopo.flying.anotherlayout.kits.imageload.PhotoManager;
import com.xiaopo.flying.anotherlayout.model.Album;
import com.xiaopo.flying.anotherlayout.model.Photo;
import com.xiaopo.flying.anotherlayout.model.PhotoHeader;
import com.xiaopo.flying.anotherlayout.ui.page.about.AboutActivity;
import com.xiaopo.flying.anotherlayout.ui.page.layout.LayoutActivity;
import com.xiaopo.flying.anotherlayout.ui.page.process.ProcessActivity;
import com.xiaopo.flying.anotherlayout.ui.page.production.ProductionActivity;
import com.xiaopo.flying.anotherlayout.ui.recycler.adapter.PuzzleAdapter;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.AlbumTitleBinder;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.PhotoBinder;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.PhotoHeaderBinder;
import com.xiaopo.flying.anotherlayout.ui.recycler.decoration.AlbumItemDecoration;
import com.xiaopo.flying.anotherlayout.ui.recycler.decoration.PhotoItemDecoration;
import com.xiaopo.flying.puzzle.slant.SlantPuzzleLayout;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

public class MainActivity extends RxAppCompatActivity
    implements WeakHandler.IHandler {
  private static final int MAX_PHOTO_COUNT = 9;
  public static final int CODE_REQUEST_PERMISSION = 110;

  @BindView(R.id.puzzle_list)
  RecyclerView puzzleList;
  @BindView(R.id.tab_layout)
  TabLayout tabLayout;
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.viewPager)
  ViewPager viewPager;
  @BindView(R.id.content_main)
  FrameLayout contentMain;

  RecyclerView photoList;
  RecyclerView albumList;

  private PuzzleAdapter puzzleAdapter;

  private MultiTypeAdapter photoAdapter;
  private MultiTypeAdapter albumAdapter;

  private ArrayMap<String, Bitmap> arrayBitmap = new ArrayMap<>();
  private ArrayList<String> selectedPath = new ArrayList<>();
  private List<Bitmap> bitmaps = new ArrayList<>();

  private Items allPhotos = new Items();
  private Items allPhotosWithAlbum = new Items();

  private int deviceSize;

  private WeakHandler puzzleHandler;

  private Set<Integer> selectedPositions = new TreeSet<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Fabric.with(this, Crashlytics.getInstance());

    // TODO 暂时去掉抽屉
    setContentView(R.layout.drawer_content_main);
    ButterKnife.bind(this);

    puzzleHandler = new WeakHandler(this);

    initView();

    deviceSize = DipPixelKit.getDeviceWidth(this);

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
    final int screenWidth = DipPixelKit.getDeviceWidth(this);
    final int availableLength = screenWidth - 3 * DipPixelKit.dip2px(this, 2);

    photoList = new RecyclerView(this);
    photoAdapter = new MultiTypeAdapter();
    PhotoBinder photoBinder = new PhotoBinder(
        selectedPositions, MAX_PHOTO_COUNT, availableLength / 4, availableLength / 4);
    photoBinder.setOnPhotoSelectedListener((photo, position) -> {
      int pos = allPhotosWithAlbum.indexOf(photo);
      albumAdapter.notifyItemChanged(pos);
      selectPhoto(photo);
    });
    photoAdapter.register(PhotoHeader.class, new PhotoHeaderBinder());
    photoAdapter.register(Photo.class, photoBinder);
    GridLayoutManager allPhotosLayoutManager = new GridLayoutManager(this, 4);
    allPhotosLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        return position == 0 ? 4 : 1;
      }
    });
    photoList.setLayoutManager(allPhotosLayoutManager);
    photoList.setAdapter(photoAdapter);

    albumList = new RecyclerView(this);
    albumAdapter = new MultiTypeAdapter();

    PhotoBinder allPhotosWithAlbumBinder = new PhotoBinder(
        selectedPositions, MAX_PHOTO_COUNT, availableLength / 4, availableLength / 4);
    allPhotosWithAlbumBinder.setOnPhotoSelectedListener((photo, position) -> {
      int pos = allPhotos.indexOf(photo);
      photoAdapter.notifyItemChanged(pos);
      selectPhoto(photo);
    });
    albumAdapter.register(Album.class, new AlbumTitleBinder());
    albumAdapter.register(Photo.class, allPhotosWithAlbumBinder);
    albumAdapter.register(PhotoHeader.class, new PhotoHeaderBinder());

    GridLayoutManager allPhotosWithAlbumLayoutManager = new GridLayoutManager(MainActivity.this, 4);
    allPhotosWithAlbumLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        if (position == 0) return 4;
        return allPhotosWithAlbum.get(position) instanceof Album ? 4 : 1;
      }
    });
    albumList.setLayoutManager(allPhotosWithAlbumLayoutManager);
    albumList.setAdapter(albumAdapter);

    List<View> viewList = Arrays.asList(photoList, albumList);
    List<String> titleList =
        Arrays.asList(getString(R.string.all_photo), getString(R.string.album));
    viewPager.setAdapter(new SelectPagerAdapter(viewList, titleList));

    //about others
    tabLayout.setupWithViewPager(viewPager);

    // Toolbar
    toolbar.inflateMenu(R.menu.menu_main_toolbar);
    toolbar.setOnMenuItemClickListener(item -> {
      Intent intent = null;
      switch (item.getItemId()) {
        case R.id.action_my_image:
          intent = new Intent(this, ProductionActivity.class);
          break;
        case R.id.action_my_layout:
          intent = new Intent(this, LayoutActivity.class);
          break;
        case R.id.action_about:
          intent = new Intent(this, AboutActivity.class);
          break;
      }
      startActivity(intent);
      return false;
    });

    // drawer layout
//    DrawerLayout drawer = findViewById(R.id.drawer_layout);
//    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
//        R.string.navigation_drawer_close);
//    drawer.addDrawerListener(toggle);
//    toggle.syncState();
//
//    NavigationView navigationView = findViewById(R.id.nav_view);
//    navigationView.setNavigationItemSelectedListener(this);
  }

  public void fetchBitmap(final String path) {
    final int screenWidth = DipPixelKit.getDeviceWidth(this);
    final int availableWidth = screenWidth - 3 * DipPixelKit.dip2px(this, 2);
    final int resize = availableWidth / 4;

    Observable.just(path)
        .compose(this.bindToLifecycle())
        .subscribeOn(Schedulers.io())
        .map(photoPath ->
            ImageEngine.instance()
                .get(this, path, resize, resize)
        ).observeOn(AndroidSchedulers.mainThread())
        .subscribe(bitmap -> {
          arrayBitmap.put(path, bitmap);
          bitmaps.add(bitmap);
          selectedPath.add(path);

          refreshLayout();
        });

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

      ImageEngine.instance()
          .prefetch(this, photo.getPath(), deviceSize, deviceSize);
    } else {
      Bitmap bitmap = arrayBitmap.remove(photo.getPath());
      bitmaps.remove(bitmap);
      selectedPath.remove(photo.getPath());

      puzzleAdapter.refreshData(PuzzleKit.getPuzzleLayouts(bitmaps.size()), bitmaps);
    }
  }

  private void loadPhoto() {
    allPhotos.clear();
    allPhotos.add(new PhotoHeader());
    allPhotosWithAlbum.clear();
    allPhotosWithAlbum.add(new PhotoHeader());
    final ArrayMap<String, List<Photo>> albumArray = new ArrayMap<>();

    Completable.wrap(completableObserver -> {
      PhotoManager photoManager = new PhotoManager(this);

      List<Photo> photos = photoManager.getAllPhotos();
      allPhotos.addAll(photos);

      final int size = photos.size();
      for (int i = 0; i < size; i++) {
        Photo photo = photos.get(i);
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
        allPhotosWithAlbum.add(album);
        allPhotosWithAlbum.addAll(list);
      }

      completableObserver.onComplete();

    }).compose(this.bindToLifecycle())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> {
          final int space = DipPixelKit.dip2px(MainActivity.this, 2);
          AlbumItemDecoration albumItemDecoration
              = new AlbumItemDecoration(allPhotosWithAlbum, space);
          albumAdapter.setItems(allPhotosWithAlbum);
          albumAdapter.notifyDataSetChanged();
          albumList.addItemDecoration(albumItemDecoration);

          PhotoItemDecoration photoItemDecoration
              = new PhotoItemDecoration(4, space, false);
          photoAdapter.setItems(allPhotos);
          photoAdapter.notifyDataSetChanged();
          photoList.addItemDecoration(photoItemDecoration);
        });

  }


  @Override
  public void handleMsg(Message msg) {
    if (msg.what == 119) {
      refreshLayout();
    } else if (msg.what == 120) {
      fetchBitmap((String) msg.obj);
    }
  }

  private class SelectPagerAdapter extends PagerAdapter {

    private final List<View> viewList;
    private final List<String> titleList;

    private SelectPagerAdapter(List<View> viewList, List<String> titleList) {
      this.viewList = viewList;
      this.titleList = titleList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      container.addView(viewList.get(position));
      return viewList.get(position);
    }

    @Override
    public int getCount() {
      return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      super.destroyItem(container, position, object);
      container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return titleList.get(position);
    }
  }
}
