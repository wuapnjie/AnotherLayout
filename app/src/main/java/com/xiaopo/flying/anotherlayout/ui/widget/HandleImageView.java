package com.xiaopo.flying.anotherlayout.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import com.xiaopo.flying.anotherlayout.R;

/**
 * @author wupanjie
 */
public class HandleImageView extends AppCompatImageView {
  private boolean needDrawArrow;
  private Paint paint;
  private Path path;
  private int arrowSize;

  public HandleImageView(Context context) {
    this(context, null, 0);
  }

  public HandleImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HandleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HandleImageView);
    arrowSize = ta.getDimensionPixelSize(R.styleable.HandleImageView_arrow_size, 10);
    ta.recycle();

    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setColor(Color.BLACK);
    paint.setStyle(Paint.Style.FILL);

    path = new Path();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (needDrawArrow) {
      final int width = getWidth();
      canvas.translate(width / 2, 0);
      path.reset();
      path.moveTo(-arrowSize, 0);
      path.lineTo(0, arrowSize);
      path.lineTo(arrowSize, 0);
      path.lineTo(-arrowSize, 0);
      canvas.drawPath(path, paint);
    }
  }

  public void setNeedDrawArrow(boolean needDrawArrow) {
    this.needDrawArrow = needDrawArrow;
    invalidate();
  }

  public boolean isNeedDrawArrow() {
    return needDrawArrow;
  }
}
