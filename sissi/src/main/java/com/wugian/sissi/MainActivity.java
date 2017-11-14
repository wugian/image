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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.wugian.sissi.utils.PreferencesUtils;
import com.wugian.sissi.view.SnowView;

import java.io.IOException;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MainActivity extends Activity implements View.OnTouchListener {
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
            "2/" + arrayPictures.length + " adfasdfasdgasdfasdfa"
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
    }


    private void loadPicture() {
        Glide.with(context)
                .load("file:///android_asset/" + arrayPictures[pictureIndex])
                .apply(requestOptions)
                .transition(withCrossFade())
                .into(imageSwitcher);
        promote.setText(arrayPromote[pictureIndex]);
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
