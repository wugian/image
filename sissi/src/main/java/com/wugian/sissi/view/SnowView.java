package com.wugian.sissi.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.wugian.sissi.R;

public class SnowView extends View {
    private static final int NUM_SNOWFLAKES = 48;
    private static final int DELAY = 5;

    private SnowFlake[] snowflakes;

    public SnowView(Context context) {
        super(context);
    }

    public SnowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadFlower();
    }

    public SnowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadFlower();
    }

    protected void resize(int width, int height) {
        loadFlower();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        snowflakes = new SnowFlake[NUM_SNOWFLAKES];
        for (int i = 0; i < NUM_SNOWFLAKES; i++) {
            snowflakes[i] = SnowFlake.create(width, height, paint, mFlowerList[i % mFlowerList.length]);
//            snowflakes[i].setFlowers(mFlowers);
        }
    }


    Bitmap mFlowers = null;
    int[] ids = new int[]{R.mipmap.ross_1, R.mipmap.rose_2, R.mipmap.rose_3, R.mipmap.rose_4};
    Bitmap[] mFlowerList = new Bitmap[ids.length];


    public void loadFlower() {
        Resources r = this.getContext().getResources();
        Drawable drawable = r.getDrawable(R.mipmap.rose_2);
        mFlowers = (((BitmapDrawable) drawable).getBitmap());
        Matrix matrix = new Matrix();
        matrix.postScale(.14f, .14f);
        // 得到新的图片
        mFlowers = Bitmap.createBitmap(mFlowers, 0, 0, mFlowers.getWidth(), mFlowers.getHeight(), matrix,
                true);
        for (int i = 0; i < ids.length; i++) {
            mFlowerList[i] = loadFlowerItem(ids[i]);
        }
    }

    private Bitmap loadFlowerItem(int id) {
        Bitmap bm = null;
        Resources r = this.getContext().getResources();
        Drawable drawable = r.getDrawable(id);
        bm = (((BitmapDrawable) drawable).getBitmap());
        Matrix matrix = new Matrix();
        matrix.postScale(.2f, .2f);
        // 得到新的图片
        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix,
                true);
        return bm;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            resize(w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (SnowFlake snowFlake : snowflakes) {
            snowFlake.draw(canvas);
        }
        getHandler().postDelayed(runnable, DELAY);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
}
