package com.ustcinfo.mobile.platform.core.utils;

import android.app.Activity;
import android.content.Intent;

import com.ustcinfo.mobile.platform.ability.application.AbilityApplication;
import com.ustcinfo.mobile.platform.core.appstore.AppStoreUtils;
import com.ustcinfo.mobile.platform.core.model.AppInfo;
import com.ustcinfo.mobile.platform.core.model.UserInfo;
import com.ustcinfo.mobile.platform.core.ui.activity.WebViewActivity;

import java.io.File;

public class WebAppUtils {

    /**
     * 检测APP是否已经安装
     */
    public static boolean isAppInstalled(AppInfo info) {
        //判断是否有启动界面的文件，存在则表示安装包已经安装
        String path = AppStoreUtils.getWebAppExtractPath() + info.packageName;
        File file = new File(path);
        return file.exists();
    }


    /**
     * 检测本地web app版本号
     */
    public static String getVersion(AppInfo info) {
        return PropertiesUtils.readValue(getPropertiesPath(info), "version");
    }


    /**
     * 获取MANIFEST.properties配置文件的路径
     */
    public static String getPropertiesPath(AppInfo info) {
        return new StringBuilder().append(AppStoreUtils.getWebAppExtractPath())
                .append(info.packageName).append(File.separator).append("manifest.properties").toString();
    }

    /**
     * 安装APP
     */
    public static void installApp(File file) throws Exception {
        ZipFileUtils.unZip(file, AppStoreUtils.getWebAppExtractPath(), null);
    }

    /**
     * 启动Web APP,离线应用
     * 启动原则：
     * 1.服务端配置的启动界面
     */
    public static void launch(Activity activity, AppInfo info) {
        Intent i = null;
        i = new Intent();
        i.setClass(activity, com.ustcinfo.mobile.platform.ability.jsbridge.WebViewActivity.class);
        i.putExtra("userId", UserInfo.get().getUserId());
        i.putExtra("type", info.type);
        i.putExtra("url", info.startUrl);
        i.putExtra("webAppExtractPath", "file://" + AppStoreUtils.getWebAppExtractPath());
        activity.startActivity(i);
    }


    /**
     * 启动Web APP,离线应用
     * 启动原则：
     * 1.服务端配置的启动界面
     */
    public static void launch(Activity activity, AppInfo info,String code,String redirect_uri) {
        Intent i = null;
        i = new Intent();
        i.setClass(activity, com.ustcinfo.mobile.platform.ability.jsbridge.WebViewActivity.class);
        i.putExtra("userId", UserInfo.get().getUserId());
        i.putExtra("type", info.type);
        i.putExtra("url", info.startUrl);
        i.putExtra("webAppExtractPath", "file://" + AppStoreUtils.getWebAppExtractPath());
        i.putExtra("code", code);
        i.putExtra("redirect_uri", redirect_uri);
        activity.startActivity(i);
    }

    /**
     * 删除根据包名删除相应的文件夹
     */
    public static void uninstall(AppInfo info) {
        if (info == null)
            return;
        String path = AppStoreUtils.getWebAppExtractPath() + info.packageName;
        FileUtil.deleteDir(new File(path));
    }


}
