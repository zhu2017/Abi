package com.ustcinfo.mobile.platform.core.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.ustcinfo.mobile.platform.core.R;
import com.ustcinfo.mobile.platform.core.appstore.AppStore;
import com.ustcinfo.mobile.platform.core.config.MConfig;
import com.ustcinfo.mobile.platform.core.constants.Constants;
import com.ustcinfo.mobile.platform.core.interfaces.HttpRequestCallbak;
import com.ustcinfo.mobile.platform.core.interfaces.LoginCallBack;
import com.ustcinfo.mobile.platform.core.model.UserInfo;
import com.ustcinfo.mobile.platform.core.utils.Key64;
import com.ustcinfo.mobile.platform.core.utils.MHttpClient;
import com.ustcinfo.mobile.platform.core.utils.MSharedPreferenceUtils;
import com.ustcinfo.mobile.platform.core.utils.RSAUtils;
import com.ustcinfo.mobile.platform.core.utils.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SunChao on 2017/5/15.
 */

public class MLogin {

    private boolean autoLogin;

    private Context context;

    private LoginCallBack callback;

    /**
     * 登陆实例化
     *
     * @param autoLogin 记录是否进行自动登陆
     */
    public MLogin(Context context, boolean autoLogin, LoginCallBack callback) {
        this.autoLogin = autoLogin;
        this.context = context;
        this.callback = callback;
    }

    /**
     * 使用用户上次登录缓存历史记录进行登录
     */
    public void loginBySSO() {
        autoLogin = MSharedPreferenceUtils.queryBooleanBySettings(context, Constants.KEY_PREFERENCE_AUTO_LOGIN);
        String userId = UserInfo.get().getUserIdCache();
        String pwd = UserInfo.get().getPasswordCache();
        if (userId != null && pwd != null)
            loginBySSO(autoLogin, userId, pwd);
        else
            goLoginActivity();
    }

    /**
     * @param autoLogin 设置下次是否为自动登陆
     */
    public void loginBySSO(boolean autoLogin, String userId, String password) {
        if (autoLogin && !TextUtils.isEmpty(userId) && !TextUtils.isEmpty(password)) {
            login(userId, "", password, true);
        } else {
            goLoginActivity();
        }
    }


    private void goLoginActivity() {
        Intent i = null;
        try {
            i = new Intent(context, Class.forName(MConfig.getActivity("login")));
            context.startActivity(i);
            ((Activity) context).finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登陆
     *
     * @param userId   账号
     * @param password 密码
     * @param isSSO    单点登录不弹出登陆进度框
     */
    public void login(String userId, String password, String verifyCode, boolean isSSO) {
        RequestParams p = new RequestParams();
        String timeTemp = String.valueOf(System.currentTimeMillis());
        MSharedPreferenceUtils.saveStringSettings(context, "loginTime", timeTemp, true);
        p.put("loginTime", timeTemp);
        p.put("userId", userId);
        if (!TextUtils.isEmpty(password))
            p.put("password", password);

        if (!TextUtils.isEmpty(verifyCode)) {
            p.put("telephoneCode", verifyCode);
        }
        //versionName 记录登录版本日志
        try {
            p.put("versionName", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (Exception e) {
        }
        p.put("city", UserInfo.get().getCityCode());
        p.put("clientType", "1");

        if (isSSO) {
            autoLogin = true;
            loginByParams(MConfig.get("loginBySSO"), p);
        } else {
            loginByParams(MConfig.get("login"), p);
        }
    }

    private void loginByParams(String url, final RequestParams params) {
        callback.onLoginStart();
        MHttpClient.get().post(url, params, new HttpRequestCallbak() {
            @Override
            public void onSuccess(JSONObject responseObj) {

                callback.onLoginSuccess(responseObj);
            }

            @Override
            public void onFailed(String msg) {
                callback.onLoginFailed(msg);
            }
        });
    }
}
