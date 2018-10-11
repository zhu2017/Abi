/******************************************************************* 
* Copyright (c) 2015 by USTC SINOVATE SOFTWARE ,Inc. 
* All rights reserved. 
* 
* This file is proprietary and confidential to USTC SINOVATE SOFTWARE. 
* No part of this file may be reproduced, stored, transmitted, 
* disclosed or used in any form or by any means other than as 
* expressly provided by the written permission from the project
* team of mobile application platform
* 
* ****************************************************************/

package com.ustcinfo.mobile.platform.core.plugin;


import com.ustcinfo.mobile.platform.core.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 
 *@author Chao Sun
 *@version 1.0
 */

public class ResultFactory {

	//请求数据格式错误
	public static final int CODE_FAILED_JSON_PARSE = 1;
	
	//安全密匙有误
	public static final int CODE_FAILED_SECURITY_KEY = 2;
	
	//未在应用市场中发布
	public static final int CODE_FAILED_NOT_PUBLISH_ON_STORE= 3;

	//未知操作
	public static final int CODE_FAILED_UNKONW_OPERATE= 4;

	//获取子应用映射信息时错误
	public static final int CODE_FAILED_MAPPING_ERROR = 100;

	//获取子应用映射信息解析错误
	public static final int CODE_FAILED_ANALIZE_ERROR = 101;

	//门户没有映射该用户数据
	public static final int CODE_FAILED_NOUSER_ERROR = 102;


	public static String createFailed(int code) {
		String desc = "The request is invalid";
		JSONObject resultObj = new JSONObject();
		try {
			resultObj.put("ret", 1);
			resultObj.put("msg", "Failed");
			JSONObject errorObj = new JSONObject();
			errorObj.put("code", code);
			switch (code) {
			case CODE_FAILED_JSON_PARSE:
				desc = "请求数据格式错误,请联系平台开发人员解决";
				break;
			case CODE_FAILED_SECURITY_KEY:
				desc = "安全密匙有误，非法请求,请联系平台开发人员解决";
				break;
			case CODE_FAILED_NOT_PUBLISH_ON_STORE:
				desc = "应用未在市场中发布，非法请求，请将APP发布至市场";
				break;
			case CODE_FAILED_UNKONW_OPERATE:
				desc = "未知操作，请联系平台开发人员";
				break;
			case CODE_FAILED_MAPPING_ERROR:
				desc = "请求子应用用户数据错误,请联系平台开发人员解决";
				break;
			case CODE_FAILED_ANALIZE_ERROR:
				desc = "子应用数据解析有误,请联系平台开发人员解决";
				break;
			case CODE_FAILED_NOUSER_ERROR:
				desc = "未查询到该用户的相关信息,请联系平台开发人员解决";
				break;
			}
			errorObj.put("desc", desc);
			resultObj.put("error", errorObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Logger.e("Failed", resultObj.toString());
		return resultObj.toString();
	}
	
	
	public static String createSuccess(JSONObject dataObj) {
		JSONObject resultObj = new JSONObject();
		try {
			resultObj.put("ret", 0);
			resultObj.put("msg", "ok");
			resultObj.put("data", dataObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Logger.i("Success", resultObj.toString());
		return resultObj.toString();
	}
}
