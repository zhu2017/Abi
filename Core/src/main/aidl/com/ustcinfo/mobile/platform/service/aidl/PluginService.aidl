// PluginService.aidl
package com.ustcinfo.mobile.platform.service.aidl;

// Declare any non-default types here with import statements

interface PluginService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
	String invoke(String operatorType ,String jsonString);

	//山东联通单点登录
	String invokeSd(String operatorType ,String jsonString);
}
