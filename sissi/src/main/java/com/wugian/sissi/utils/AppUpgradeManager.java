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
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Created by wugian on 2017/4/24
 */
public class AppUpgradeManager {
    private static final String TAG = "AppUpgradeManager";
    private static final int SHOW_DIALOG_UPDATE = 0x91;
    private static final int SHOW_DIALOG_NEWEST = 0x92;
    private String updateApkUrl = "https://raw.githubusercontent.com/wugian/abc/master/cinema/sissi_config";
    private UpdateMsg mUpdateMsg;
    private Context context;
    private String packageName = "";
    private boolean showUpdate;
    private Handler mHandler;

    public AppUpgradeManager(Context context, String packageName, boolean showUpdates) {
        this.context = context;
        this.packageName = packageName;
        this.showUpdate = showUpdates;
        this.mHandler = new AppUpdateHandler();
    }

    private class AppUpdateHandler extends Handler {

        private AppUpdateHandler() {

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DIALOG_UPDATE:
                    doNewVersionUpdate();
                    break;
                case SHOW_DIALOG_NEWEST:
                    notNewVersionShow();
                    break;
            }
        }
    }


    public boolean updateAppVersion() {
        String verJson = null;
        Log.d("install", "request:" + updateApkUrl);
        try {
            verJson = OkHttpClientManager.getAsString(updateApkUrl);
            Log.d("install", "result:" + verJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(verJson)) {
            Log.d("install", "verJson charge empty");
            return false;
        }
        try {
            UpdateMsg updateMsg = new Gson().fromJson(verJson, new TypeToken<UpdateMsg>() {
            }.getType());
            if (updateMsg != null && updateMsg.getCode() == 0
                    && updateMsg.getData() != null) {
                int currentVersionCode = getVerCode(context);
                int onlineVersionCode = updateMsg.getData().getVerCode();
                Log.d("install", "charge version:currentVersion:" + currentVersionCode + ",onlineVersion:" + onlineVersionCode);
                if (onlineVersionCode > currentVersionCode) {
                    this.mUpdateMsg = updateMsg;
                    mHandler.sendEmptyMessage(SHOW_DIALOG_UPDATE);
                } else {
                    Log.d("install", "charge empty");
                    if (showUpdate) {
                        mHandler.sendEmptyMessage(SHOW_DIALOG_NEWEST);
                    }
                }
            } else {
                Log.d("install", "charge 123");
                if (showUpdate) {
                    mHandler.sendEmptyMessage(SHOW_DIALOG_NEWEST);
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return true;
    }


    private void doNewVersionUpdate() {
        Log.d("install", "doNewVersionUpdate");
        if (!showUpdate && mUpdateMsg.getData().isForceType()) {
            String url = mUpdateMsg.getData().getAppUrl();
            Log.d("install", "doNewVersionUpdate:" + url);
            if (!TextUtils.isEmpty(url)) {
                UpgradeDownloadManager updateManager =
                        new UpgradeDownloadManager(context, url, mUpdateMsg.getData().isForceType());
                updateManager.downloadApk(showUpdate | !mUpdateMsg.getData().isForceType());
            }
            return;
        }
        String verName = getVerName(context);
        StringBuilder message = new StringBuilder();
        message.append("当前版本");
        message.append(verName);
        message.append(", 发现新版本");
        message.append(mUpdateMsg.getData().getVerName());
        message.append(", 是否更新?");
        message.append("\n");
        String description = mUpdateMsg.getData().getDescription();
        if (!TextUtils.isEmpty(description)) {
            if (description.contains("#")) {
                String[] split = description.split("#");
                for (String s : split) {
                    message.append("\n").append(s);
                }
            } else {
                message.append("\n").append(mUpdateMsg);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        AlertDialog dialog = builder.setTitle("软件更新")
                .setMessage(message.toString())
//                .setCancelable(false)
                // 设置内容
                .setPositiveButton("更新",// 设置确定按钮
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String url = mUpdateMsg.getData().getAppUrl();
                                if (!TextUtils.isEmpty(url)) {
                                    UpgradeDownloadManager updateManager =
                                            new UpgradeDownloadManager(context, url, showUpdate & mUpdateMsg.getData().isForceType());
                                    updateManager.downloadApk(showUpdate | !mUpdateMsg.getData().isForceType());
                                } else {
                                    dialogInterface.dismiss();
                                }
                            }
                        }).create();
        if (dialog.getWindow() != null) {
            if (!dialog.isShowing()) {//此时提示框未显示
                dialog.show();
            }
        }
    }

    private void notNewVersionShow() {
        String verName = getVerName(context);
        String string = "当前版本" +
                verName +
                "已是最新版本";
        Dialog dialog = new AlertDialog.Builder(context)
                .setTitle("软件更新")
                .setMessage(string)// 设置内容
                .setPositiveButton("确定",// 设置确定按钮
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int which) {
                                dialog1.dismiss();
                            }
                        }).create();// 创建
        // 显示对话框
        dialog.show();
    }


    private int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    packageName, 0).versionCode;
            Log.d("install", packageName + ":" + verCode);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verCode;
    }

    private String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    packageName, 0).versionName;
            Log.d("install", packageName + ":" + verName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verName;
    }
}
