package com.xiaopo.flying.anotherlayout.ui.page.process;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.anotherlayout.kits.imageload.ImageEngine;
import com.xiaopo.flying.anotherlayout.model.data.Style;
import com.xiaopo.flying.anotherlayout.ui.widget.PhotoPuzzleView;
import com.xiaopo.flying.puzzle.PuzzleLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * @author wupanjie
 */
public class LoadPhotosObservables {

  public static Observable<List<Pair<Bitmap, String>>> loadWithStyle(
      final Context context, final Style style) {
    return Observable.create((ObservableOnSubscribe<List<Pair<Bitmap, String>>>) emitter -> {
      if (!style.getPieces().isPresent()) return;

      final int deviceSize = DipPixelKit.getDeviceWidth(context);
      final Map<String, Bitmap> pathBitmaps = new HashMap<>();

      List<PhotoPuzzleView.PieceInfo> infos = style.getPieces().get().pieces;
      final int count = infos.size();
      final List<Pair<Bitmap, String>> results = new ArrayList<>(count);

      for (int i = 0; i < count; i++) {
        final String photoPath = infos.get(i).path;

        // prefetch next photo
        if (i < count - 1) {
          ImageEngine.instance()
              .prefetch(context, infos.get(i + 1).path, deviceSize, deviceSize);
        }

        if (!pathBitmaps.containsKey(photoPath)) {
          Bitmap bitmap = ImageEngine.instance()
              .get(context, photoPath, deviceSize, deviceSize);
          pathBitmaps.put(photoPath, bitmap);
        }

        results.add(
            new Pair<>(pathBitmaps.get(photoPath), photoPath));

      }

      emitter.onNext(results);
      emitter.onComplete();
    }).subscribeOn(Schedulers.io());
  }

  public static Observable<List<Pair<Bitmap, String>>> loadWithPaths(
      final Context context, final PuzzleLayout puzzleLayout, final List<String> bitmapPaths) {
    return Observable.create((ObservableOnSubscribe<List<Pair<Bitmap, String>>>) emitter -> {
      final int deviceSize = DipPixelKit.getDeviceWidth(context);

      final int count = bitmapPaths.size() > puzzleLayout.getAreaCount()
          ? puzzleLayout.getAreaCount() : bitmapPaths.size();

      final List<Pair<Bitmap, String>> results = new ArrayList<>(count);
      final Map<String, Bitmap> pathBitmaps = new HashMap<>();

      for (int i = 0; i < count; i++) {
        final String photoPath = bitmapPaths.get(i);

        if (!pathBitmaps.containsKey(photoPath)) {
          Bitmap bitmap = ImageEngine.instance()
              .get(context, photoPath, deviceSize, deviceSize);
          pathBitmaps.put(photoPath, bitmap);
        }

        results.add(
            new Pair<>(pathBitmaps.get(photoPath), photoPath));

      }

      emitter.onNext(results);
      emitter.onComplete();
    }).subscribeOn(Schedulers.io());
  }
}
