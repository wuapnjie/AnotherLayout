package com.xiaopo.flying.anotherlayout.ui.page.process;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.xiaopo.flying.anotherlayout.kits.DipPixelKit;
import com.xiaopo.flying.anotherlayout.kits.imageload.ImageEngine;
import com.xiaopo.flying.anotherlayout.model.PieceInfo;
import com.xiaopo.flying.anotherlayout.model.database.Style;
import com.xiaopo.flying.puzzle.PuzzleLayout;

import java.io.File;
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
class LoadPhotosObservables {

  static Observable<List<Pair<Bitmap, String>>> loadWithStyle(
      final Context context, final Style style) {
    return Observable.create((ObservableOnSubscribe<List<Pair<Bitmap, String>>>) emitter -> {
      if (!style.getPieces().isPresent()) return;

      final int deviceSize = DipPixelKit.getDeviceWidth(context);
      final Map<String, Bitmap> pathBitmaps = new HashMap<>();

      List<PieceInfo> infos = style.getPieces().get().pieces;
      final int count = infos.size();
      final List<Pair<Bitmap, String>> results = new ArrayList<>(count);

      for (int i = 0; i < count; i++) {
        final String photoPath = infos.get(i).path;

        File file = new File(photoPath);

        // prefetch next photo
        if (i < count - 1) {
          File next = new File(infos.get(i + 1).path);
          if (next.exists()) {
            ImageEngine.instance()
                .prefetch(context, next, deviceSize, deviceSize);
          }
        }

        if (!file.exists()) {
          Log.d("Photo", "loadWithStyle: file is not existed! --> " + photoPath);
          results.add(new Pair<>(null, photoPath));
          continue;
        }

        if (!pathBitmaps.containsKey(photoPath)) {
          Bitmap bitmap = ImageEngine.instance()
              .get(context, file, deviceSize, deviceSize);
          pathBitmaps.put(photoPath, bitmap);
        }

        results.add(
            new Pair<>(pathBitmaps.get(photoPath), photoPath));

      }

      emitter.onNext(results);
      emitter.onComplete();
    }).subscribeOn(Schedulers.io());
  }

  static Observable<List<Pair<Bitmap, String>>> loadWithPaths(
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
