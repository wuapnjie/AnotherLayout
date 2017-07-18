/*
 * Copyright 2016 drakeet. https://github.com/drakeet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaopo.flying.anotherlayout.ui.recycler.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.xiaopo.flying.poiphoto.datatype.Album;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author drakeet
 */
public class AlbumItemDecoration extends RecyclerView.ItemDecoration {

  private int space;
  private int albumPosition;
  private Set<Integer> dontNeedSet = new HashSet<>();

  public AlbumItemDecoration(List<?> data, int space) {
    this.space = space;

    for (int i = 0; i < data.size(); i++) {
      if (data.get(i) instanceof Album) {
        albumPosition = i;
      }
      if ((i - albumPosition) % 4 == 1) {
        dontNeedSet.add(i);
      }
    }
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    int position = parent.getChildLayoutPosition(view);
    int column = 0; // item column
    if (dontNeedSet.contains(position)) {
      column = 0;
    } else if (dontNeedSet.contains(position - 1)) {
      column = 1;
    } else if (dontNeedSet.contains(position - 2)) {
      column = 2;
    } else if (dontNeedSet.contains(position - 3)) {
      column = 3;
    }

    outRect.left = column * space / 4; // column * ((1f / spanCount) * space)
    outRect.right =
        space - (column + 1) * space / 4; // space - (column + 1) * ((1f /    spanCount) * space)
    if (position > 0) {
      outRect.top = space; // item top
    }
  }
}
