package com.wugian.sissi;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.wugian.sissi.utils.AppUpgradeManager;
import com.wugian.sissi.utils.PreferencesUtils;
import com.wugian.sissi.view.SnowView;

import java.io.IOException;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MainActivity extends Activity implements View.OnTouchListener {
    private static final String UPDATE_URL = "https://raw.githubusercontent.com/wugian/abc/master/cinema/config";
    private ImageView imageSwitcher;
    private SnowView snowView;
    private TextView promote;
    private int pictureIndex = 0;
    private Context context;
    private String[] arrayPictures = {
            "dd.png",
            "cc.png"
    };
    private String[] arrayPromote = {
            "1/" + arrayPictures.length + " 当时真的很开",
            "2/" + arrayPictures.length + " 长长的沉默里，苏仪轻声道：“哥哥，嫂嫂她，是怎么样的？”" +
                    "洞中只闻松脂燃烧时微弱的“噼啪”声。他的声音低低响起：“很会跟我撒娇，偶尔耍耍小脾气，经常哭鼻子。” " +
                    "苏仪顿了顿：“若是这样的小姐，天下到处都是，哥哥你何苦……” " +
                    "他转过身来：“那是我在的时候。”没什么表情地俯身收拾石案上的琴具： " +
                    "“我不在的时候，她比谁都坚强。”"
    };
    private RequestOptions requestOptions;


    private void assignViews() {
        pictureIndex = PreferencesUtils.getInt(this, "index");
        if (pictureIndex > arrayPictures.length || pictureIndex < 0) {
            pictureIndex = 0;
        }
        context = this;
        mpMediaPlayer = new MediaPlayer();
        mpMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play();
            }
        });
        imageSwitcher = (ImageView) findViewById(R.id.imageSwitcher);
        snowView = (SnowView) findViewById(R.id.snowView);
        promote = (TextView) findViewById(R.id.promote);
        requestOptions = new RequestOptions()
                .centerInside()
                .error(R.mipmap.aaa)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        loadPicture();
        snowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureIndex = pictureIndex == arrayPictures.length - 1 ? 0
                        : pictureIndex + 1;
                loadPicture();
            }
        });
        snowView.setOnTouchListener(this);
        play();
        final AppUpgradeManager appUpgradeManager = new AppUpgradeManager(MainActivity.this, "com.wugian.sissi", true);
        new Thread() {
            @Override
            public void run() {
                super.run();
                appUpgradeManager.updateAppVersion();
            }
        }.start();
    }


    private void loadPicture() {
        Glide.with(context)
                .load("file:///android_asset/" + arrayPictures[pictureIndex])
                .apply(requestOptions)
                .transition(withCrossFade())
                .into(imageSwitcher);
        promote.setText(toDBC(arrayPromote[pictureIndex]));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "version 2", Toast.LENGTH_LONG).show();
        assignViews();
    }


    public String toDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    private boolean right() {
        // 取得当前要看的图片的index
        pictureIndex = pictureIndex == arrayPictures.length - 1 ? 0
                : pictureIndex + 1;
        loadPicture();
        return true;
    }

    private boolean left() {
        pictureIndex = pictureIndex == 0 ? arrayPictures.length - 1
                : pictureIndex - 1;
        loadPicture();
        return true;
    }

    private float touchDownX;
    private float touchUpX;
    private static final int MIN_SLIDE = 100;

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
            if (touchUpX - touchDownX > MIN_SLIDE) {
                // 取得当前要看的图片的index
                left();
                // 从右往左，看下一张
            } else if (touchDownX - touchUpX > MIN_SLIDE) {
                // 取得当前要看的图片的index
                right();
            } else {
                v.performClick();
            }
            return true;
        }
        return false;
    }

    MediaPlayer mpMediaPlayer;

    protected void play() {
        AssetManager am = getAssets();
        try {
            AssetFileDescriptor fd = am.openFd("music.m4a");
            mpMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mpMediaPlayer.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mpMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mpMediaPlayer.start();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mpMediaPlayer != null) {
            mpMediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mpMediaPlayer != null) {
            mpMediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferencesUtils.putInt(this, "index", pictureIndex);
    }
}
