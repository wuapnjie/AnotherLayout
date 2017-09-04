package com.xiaopo.flying.anotherlayout.ui.page.mylayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaopo.flying.anotherlayout.R;
import com.xiaopo.flying.anotherlayout.model.data.Stores;
import com.xiaopo.flying.anotherlayout.model.data.Style;
import com.xiaopo.flying.anotherlayout.ui.recycler.binder.PuzzleLayoutBinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wupanjie
 */
public class MyLayoutActivity extends AppCompatActivity {

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.puzzle_list) RecyclerView puzzleList;

  private MultiTypeAdapter adapter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_layout);
    ButterKnife.bind(this);

    puzzleList.setLayoutManager(new LinearLayoutManager(this));

    adapter = new MultiTypeAdapter();
    adapter.register(Style.class, new PuzzleLayoutBinder());
    puzzleList.setAdapter(adapter);

    Stores.instance(this)
        .getAllStyles()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(styles -> {
          adapter.setItems(new Items(styles));
          adapter.notifyDataSetChanged();
        });

  }
}
