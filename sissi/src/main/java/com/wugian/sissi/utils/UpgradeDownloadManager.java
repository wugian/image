/*
 *  Copyright (c)  2017.  wugian
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.wugian.sissi.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.squareup.okhttp.Request;

import java.io.File;

class UpgradeDownloadManager {
    private Context mContext;
    private String apkUrl;
    private Dialog downloadDialog;
    private ProgressBar mProgress;
    private int progress;

    UpgradeDownloadManager(Context context, String apkMessage, boolean silent) {
        this.mContext = context;
        this.apkUrl = apkMessage;
    }

    private boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    private String getRootFilePath() {
        if (hasSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + File.separator + "test/"
                  /* + "android" + File.separator + "data" + File.separator*/;
        } else {
            return Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + "data" + File.separator;
        }
    }

    void downloadApk(boolean showDialog) {
        if (showDialog) {
            mProgress = new ProgressBar(mContext, null,
                    android.R.attr.progressBarStyleHorizontal);//水平条形
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(200, -2);//长度200，高度默认
            mProgress.setLayoutParams(lp2);
            mProgress.setMax(100);

            downloadDialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("软件下载中......")
                    .setView(mProgress)
                    .setCancelable(true)
//                    .setCancelable(false)
                    // 设置内容
                    .create();// 创建
            downloadDialog.show();
        }
        downloadApk();
    }

    private void downloadApk() {
        String appName = apkUrl;
        if (apkUrl.contains("/")) {
            final String[] split = apkUrl.split("/");
            appName = split[split.length - 1];
        }
        if (!new File(getRootFilePath()).exists()) {
            final File file = new File(getRootFilePath());
            file.mkdirs();
        }
        OkHttpClientManager.downloadAsyn(apkUrl, getRootFilePath() + appName,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        if (downloadDialog != null) {
                            downloadDialog.dismiss();
                        }
                        installApk(response);
                    }
                }, new OkHttpClientManager.ProgressListener() {
                    @Override
                    public void onResponseProgress(long bytesRead, long contentLength) {
                        progress = (int) (((float) bytesRead / contentLength) * 100);
                        Log.d("install", "下载:" + progress + ".....");

                        //更新进度
                        if (mProgress != null) {
                            mProgress.setProgress(progress);
                        }
                    }
                });
    }

    private void installApk(final String path) {
        File apkFile = new File(path);
        if (!apkFile.exists()) {
            return;
        }
        Log.d("install", "正在普通安装");
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, "com.wugian.sissi" + ".fileProvider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
    }
}  