package com.ustcinfo.mobile.platform.core.plugin;

import android.text.TextUtils;


import com.ustcinfo.mobile.platform.core.config.MConfig;
import com.ustcinfo.mobile.platform.core.model.UserInfo;
import com.ustcinfo.mobile.platform.core.utils.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SunChao on 2017/5/25.
 */

public class RemoteInterAdapter {

    public static final String AUTH_ACCESS_TOKEN = "access_token";
    static String result = null;
    static String packagename = null;

    public static String invoke(String optr, String jsonStr) {
        JSONObject obj = new JSONObject();
        try {
            if (TextUtils.equals(optr, AUTH_ACCESS_TOKEN)) {
                obj.put("userId", UserInfo.get().getUserId());
                obj.put("areaId", UserInfo.get().getAreaCode());
                obj.put("telephoneNumber", UserInfo.get().getTelephoneNumber());
                obj.put("password", UserInfo.get().getPasswordCache());
                return ResultFactory.createSuccess(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ResultFactory.createFailed(ResultFactory.CODE_FAILED_UNKONW_OPERATE);
    }

    //山东联通
    public static String invoke(String optr, String jsonStr,String pkg) {
        packagename = pkg;
        if (TextUtils.equals(optr, AUTH_ACCESS_TOKEN)) {
            RequestParams params = new RequestParams();

            Thread t = new Thread(runnable);
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return  result;

        }
        return ResultFactory.createFailed(ResultFactory.CODE_FAILED_UNKONW_OPERATE);

    }

    static Runnable runnable = new Runnable(){
        @Override
        public void run() {
            String userId = UserInfo.get().getUserIdCache();
            if (TextUtils.isEmpty(userId)){
                result = ResultFactory.createFailed(ResultFactory.CODE_FAILED_MAPPING_ERROR);
                return;

            }
            OkHttpClient okHttpClient = new OkHttpClient();
            //Form表单格式的参数传递
            FormBody formBody = new FormBody
                    .Builder()
                    .add("userId",userId)
                    .add("pkg",packagename)
                    .build();
            Request request = new Request
                    .Builder()
                    .post(formBody)
                    .url(MConfig.get("childAppMapping"))
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                result = response.body().string();
                response.body().close();
                if (result != null){
                    JSONObject obj = new JSONObject();
                    com.alibaba.fastjson.JSONObject dataObj = com.alibaba.fastjson.JSONObject.parseObject(result);
                    String code = dataObj.getString("code");
                    if ("登录成功".equals(code)){
                        String sysUserId = dataObj.getJSONObject("data").getString("sysUserId");
                        String tel = dataObj.getJSONObject("data").getString("tel");
                        String  emial= dataObj.getJSONObject("data").getString("emial");
                        try {
                            obj.put("code",code);
                            obj.put("sysUserId",sysUserId);
                            obj.put("tel",tel);
                            obj.put("emial",emial);
                            result =  ResultFactory.createSuccess(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            result = ResultFactory.createFailed(ResultFactory.CODE_FAILED_ANALIZE_ERROR);
                        }

                    }else{
                        result = ResultFactory.createFailed(ResultFactory.CODE_FAILED_NOUSER_ERROR);
                    }
                }else{
                    result = ResultFactory.createFailed(ResultFactory.CODE_FAILED_MAPPING_ERROR);
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = ResultFactory.createFailed(ResultFactory.CODE_FAILED_MAPPING_ERROR);
            }

        }
    };
}
