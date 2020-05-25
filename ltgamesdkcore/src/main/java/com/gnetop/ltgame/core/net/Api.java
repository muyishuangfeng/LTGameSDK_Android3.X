package com.gnetop.ltgame.core.net;


import android.app.Activity;

import com.gnetop.ltgame.core.net.retrofit.RetrofitClient;


public class Api extends RetrofitClient {

    //测试服务器
    public static final String TEST_SERVER_URL="http://";
    //测试服务器
    public static final String TEST_SERVER_DOMAIN=".sdk.appcpi.com";

    //正式服务器
    public static final String FORMAL_SERVER_URL="http://";
    //正式服务器
    public static final String FORMAL_SERVER_DOMAIN=".sdk.appcpi.com";
    //微信域名
    public static final String WX_BASE_URL = "https://api.weixin.qq.com/";

    private Api(String baseUrl) {
        super(baseUrl);
    }

    /**
     * 单例
     *
     * @param BaseUrl url
     */
    public static RetrofitService getInstance(Activity activity, String BaseUrl) {
        return new Api(BaseUrl).getRetrofit(activity).create(RetrofitService.class);
    }


}
