package com.wugian.sissi;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;

public class MainActivity extends Activity implements ViewSwitcher.ViewFactory, View.OnTouchListener {


    private ImageSwitcher imageSwitcher;
    private int pictureIndex = 0;
    private Context context;
    // 图片数组
    private String[] arrayPictures = {"lesan.png","xxsg.png"};
    private boolean localCache = false;
    private String cacheFolder = "";

    private void assignViews() {
        context = this;

        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setBackgroundColor(0xFF000000);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                imageView.setImageResource(R.mipmap.rose_heart);
                Glide.with(context.getApplicationContext())
                        .load("file:///android_asset/"+arrayPictures[pictureIndex]).into(imageView);
                return imageView;
            }
        });
        imageSwitcher.setOnTouchListener(this);
    }


    private void loadPicture(ImageSwitcher imageView) {
        imageView.setImageURI(Uri.parse("file:///android_asset/"+arrayPictures[pictureIndex]));
//        Glide.with(context).load("file:///android_asset/"+arrayPictures[0]).into(imageView.ge);

//        imageView.setImageResource(arrayPictures[pictureIndex]);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
    }


    private boolean right() {
        // 取得当前要看的图片的index
        pictureIndex = pictureIndex == arrayPictures.length - 1 ? 0
                : pictureIndex + 1;
        // 设置图片切换的动画
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                R.anim.slide_out_left));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.slide_in_right));
        // 设置图片切换的动画
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));
        // 设置当前要看的图片
//        imageSwitcher.setImageResource(arrayPictures[pictureIndex]);
        loadPicture(imageSwitcher);
        return true;
    }

    private boolean left() {
        pictureIndex = pictureIndex == 0 ? arrayPictures.length - 1
                : pictureIndex - 1;
        // 设置图片切换的动画
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right));
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));
        // 设置当前要看的图片
//        imageSwitcher.setImageResource(arrayPictures[pictureIndex]);
        loadPicture(imageSwitcher);
        return true;
    }

    // 左右滑动时手指按下的X坐标
    private float touchDownX;
    // 左右滑动时手指松开的X坐标
    private float touchUpX;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 取得左右滑动时手指按下的X坐标
            touchDownX = event.getX();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // 取得左右滑动时手指松开的X坐标
            touchUpX = event.getX();
            // 从左往右，看前一张
            if (touchUpX - touchDownX > 100) {
                // 取得当前要看的图片的index
                left();
                // 从右往左，看下一张
            } else if (touchDownX - touchUpX > 100) {
                // 取得当前要看的图片的index
                right();
            }
            return true;
        }
        return false;
    }

    @Override
    public View makeView() {
        return null;
    }
}
