package com.ustcinfo.mobile.platform.core.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.ustcinfo.mobile.platform.core.log.Logger;
import com.ustcinfo.mobile.platform.core.model.AppInfo;
import com.ustcinfo.mobile.platform.core.model.UserInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NativeAppUtils {

    /**
     * 检测APP是否已经安装
     */
    public static boolean isInstalled(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 卸载应用程序
     */
    public static void uninstall(Context context, AppInfo info) {
        Intent uninstall_intent = new Intent();
        uninstall_intent.setAction(Intent.ACTION_DELETE);
        uninstall_intent.setData(Uri.parse("package:" + info.packageName));
        context.startActivity(uninstall_intent);
    }

    /**
     * 安装APP
     */
    public static void installApp(Context context, File file,int requestCode) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Android 7.0系统文件跨进程访问做了安全限制，改成content provider方式提供
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, "com.ustcinfo.mobile.platform.fileProvider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        }else{
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        ((Activity) context).startActivityForResult(intent, requestCode);
    }


    /**
     * 检测APP版本号
     */
    public static String getVersion(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
            return info.versionName;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 启动原生APP
     * 启动顺序原则：
     * 1.在Manifest文件中， intent-filter，配置MAIN 和 LAUNCHER 的Activity
     * 2.在Manifest文件中， intent-filter，配置工程包名的Activity
     * 3.服务端配置的启动界面
     */
    public static void launch(Activity activity, AppInfo info) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(info.packageName);
        List<ResolveInfo> apps = activity.getPackageManager().queryIntentActivities(mainIntent, 0);
        if (apps.size() > 0) {
            String packageName = apps.get(0).activityInfo.packageName;
            intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        } else {
            Intent conIntent = new Intent(info.packageName, null);
            conIntent.setPackage(info.packageName);
            apps = activity.getPackageManager().queryIntentActivities(conIntent, 0);

            String packageName = null;
            String className = null;

            if (apps.size() > 0) {
                packageName = apps.get(0).activityInfo.packageName;
                className = apps.get(0).activityInfo.name;
            } else {
                packageName = info.packageName;
                className = info.startUrl;
            }
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            //一个应用想要再平台上显示多个图标时，需要到启动界面
        }
        intent.putExtra("launchClass", info.startUrl);
        intent.putExtra("title", info.name);
        //安徽：即销即装、一键缴费 需要添加加密字串
        if (TextUtils.equals("com.tydic.SalePhone", info.packageName) || TextUtils.equals("com.sale.tydic.salepayphotoapp", info.packageName)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmm");
            String currTime = df.format(new Date());
            String currSingData = new StringBuilder().append(UserInfo.get().getUserId()).append(currTime).append("2018198hfvci56").toString();
            String key = MD5Encrypt.MD5Encode(currSingData);
            Logger.d("NativeAppLaunch", "intent put secretKey-->" + key);
            intent.putExtra("secretKey", key);
        }
        activity.startActivity(intent);
    }


    /**
     * 启动原生APP
     * 启动顺序原则：
     * 1.在Manifest文件中， intent-filter，配置MAIN 和 LAUNCHER 的Activity
     * 2.在Manifest文件中， intent-filter，配置工程包名的Activity
     * 3.服务端配置的启动界面
     */
    public static void launch(Activity activity, AppInfo info,String code,String redirect_uri) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(info.packageName);
        List<ResolveInfo> apps = activity.getPackageManager().queryIntentActivities(mainIntent, 0);
        if (apps.size() > 0) {
            String packageName = apps.get(0).activityInfo.packageName;
            intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        } else {
            Intent conIntent = new Intent(info.packageName, null);
            conIntent.setPackage(info.packageName);
            apps = activity.getPackageManager().queryIntentActivities(conIntent, 0);

            String packageName = null;
            String className = null;

            if (apps.size() > 0) {
                packageName = apps.get(0).activityInfo.packageName;
                className = apps.get(0).activityInfo.name;
            } else {
                packageName = info.packageName;
                className = info.startUrl;
            }
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            //一个应用想要再平台上显示多个图标时，需要到启动界面
        }
        intent.putExtra("launchClass", info.startUrl);
        intent.putExtra("title", info.name);
        //安徽：即销即装、一键缴费 需要添加加密字串
        if (TextUtils.equals("com.tydic.SalePhone", info.packageName) || TextUtils.equals("com.sale.tydic.salepayphotoapp", info.packageName)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmm");
            String currTime = df.format(new Date());
            String currSingData = new StringBuilder().append(UserInfo.get().getUserId()).append(currTime).append("2018198hfvci56").toString();
            String key = MD5Encrypt.MD5Encode(currSingData);
            Logger.d("NativeAppLaunch", "intent put secretKey-->" + key);
            intent.putExtra("secretKey", key);
        }
        intent.putExtra("code", code);
        intent.putExtra("redirect_uri", redirect_uri);
        activity.startActivity(intent);
    }
}
