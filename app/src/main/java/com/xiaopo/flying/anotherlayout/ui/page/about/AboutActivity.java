package com.xiaopo.flying.anotherlayout.ui.page.about;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaopo.flying.anotherlayout.BuildConfig;
import com.xiaopo.flying.anotherlayout.R;

import me.drakeet.multitype.Items;
import me.drakeet.support.about.AbsAboutActivity;
import me.drakeet.support.about.Card;
import me.drakeet.support.about.Category;
import me.drakeet.support.about.Contributor;
import me.drakeet.support.about.License;
import me.drakeet.support.about.Line;

/**
 * @author wupanjie
 */
public class AboutActivity extends AbsAboutActivity {

  @Override
  protected void onCreateHeader(ImageView icon, TextView slogan, TextView version) {
    setHeaderContentColor(getResources().getColor(R.color.textColorPrimary));
    setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
    icon.setImageResource(R.mipmap.ic_launcher);
    slogan.setText("Another Layout By wupanjie");
    String versionString = "Version " + BuildConfig.VERSION_NAME;
    version.setText(versionString);
  }

  @Override
  protected void onItemsCreated(@NonNull Items items) {
    items.add(new Category("介绍与帮助"));
    items.add(new Card(getString(R.string.card_content), "分享"));

    items.add(new Category("Developer"));
    items.add(new Contributor(R.mipmap.ic_launcher, "wupanjie", "Developer & designer",
        "https://github.com/wuapnjie"));

    items.add(new Line());

    items.add(new Category("Open Source Licenses"));
    items.add(new License("Picasso", "Square", License.APACHE_2,
        "https://github.com/square/picasso"));
    items.add(new License("SqlBrite", "Square", License.APACHE_2,
        "https://github.com/square/sqlbrite"));
    items.add(new License("RxJava2", "ReactiveX", License.APACHE_2,
        "https://github.com/ReactiveX/RxJava"));
    items.add(new License("RxAndroid", "ReactiveX", License.APACHE_2,
        "https://github.com/ReactiveX/RxAndroid"));
    items.add(new License("Gson", "Google", License.APACHE_2,
        "https://github.com/google/gson"));
    items.add(new License("AndPermission", "drakeet", License.APACHE_2,
        "https://github.com/yanzhenjie/AndPermission"));
    items.add(new License("ButterKnife", "JakeWh", License.APACHE_2,
        "https://github.com/JakeWharton/butterknife"));
    items.add(new License("MultiType", "drakeet", License.APACHE_2,
        "https://github.com/drakeet/MultiType"));
    items.add(new License("about-page", "drakeet", License.APACHE_2,
        "https://github.com/drakeet/about-page"));
  }

  @Override
  protected void onActionClick(View action) {
    super.onActionClick(action);

  }
}
