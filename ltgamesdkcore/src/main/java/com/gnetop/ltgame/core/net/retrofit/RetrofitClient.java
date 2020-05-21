package com.gnetop.ltgame.core.net.retrofit;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.common.LTGameCommon;
import com.gnetop.ltgame.core.common.LTGameOptions;
import com.gnetop.ltgame.core.net.Api;
import com.gnetop.ltgame.core.net.conver.GsonConverterFactory;
import com.gnetop.ltgame.core.util.PreferencesUtils;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient implements BaseApi {
    private volatile static Retrofit retrofit = null;
    private Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
    private OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
    private static final String SDK_TEST = "1";
    private Activity activity;

    public RetrofitClient(String baseUrl) {
        retrofitBuilder.addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpBuilder.addInterceptor(getLoggerInterceptor())
                        //处理多BaseUrl,添加应用拦截器
                        .addInterceptor(new MoreBaseUrlInterceptor())
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build())
                .baseUrl(baseUrl);
    }

    /**
     * 构建retroft
     *
     * @return Retrofit对象
     */
    @Override
    public Retrofit getRetrofit(Activity activity) {
        this.activity = activity;
        if (retrofit == null) {
            //锁定代码块
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    retrofit = retrofitBuilder.build(); //创建retrofit对象
                }
            }
        }
        return retrofit;

    }


    @Override
    public OkHttpClient.Builder setInterceptor(Interceptor interceptor) {
        return httpBuilder.addInterceptor(interceptor);
    }

    @Override
    public Retrofit.Builder setConverterFactory(Converter.Factory factory) {
        return retrofitBuilder.addConverterFactory(factory);
    }

    /**
     * 拦截器
     */
    public class MoreBaseUrlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            //获取原始的originalRequest
            Request originalRequest = chain.request();
            //获取老的url
            HttpUrl oldUrl = originalRequest.url();
            //获取originalRequest的创建者builder
            Request.Builder builder = originalRequest.newBuilder();
            //获取头信息的集合如：manage,mdffx
            List<String> urlnameList = originalRequest.headers("urlname");
            if (urlnameList != null && urlnameList.size() > 0) {
                //删除原有配置中的值,就是namesAndValues集合里的值
                builder.removeHeader("urlname");
                //获取头信息中配置的value,如：manage或者mdffx
                String urlname = urlnameList.get(0);
                HttpUrl baseURL = null;
                //根据头信息中配置的value,来匹配新的base_url地址
                if ("manage".equals(urlname)) {
                    LTGameOptions options = LTGameCommon.getInstance().options();
                    String mServerTest = "";
                    String mLtAppID = "";
                    if (!TextUtils.isEmpty(options.getISServerTest())) {
                        mServerTest = options.getISServerTest();
                    } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_SERVER_TEST_TAG))) {
                        mServerTest = PreferencesUtils.getString(activity, Constants.LT_SDK_SERVER_TEST_TAG);
                    }
                    if (!TextUtils.isEmpty(options.getLtAppId())) {
                        mLtAppID = options.getLtAppId();
                    } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_APP_ID))) {
                        mLtAppID = PreferencesUtils.getString(activity, Constants.LT_SDK_APP_ID);
                    }
                    if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                        baseURL = HttpUrl.parse(Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN);
                    } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                        baseURL = HttpUrl.parse(Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN);
                    }
                } else if ("mdffx".equals(urlname)) {
                    baseURL = HttpUrl.parse(Api.WX_BASE_URL);
                }
                //重建新的HttpUrl，需要重新设置的url部分
                HttpUrl newHttpUrl = oldUrl.newBuilder()
                        .scheme(baseURL.scheme())//http协议如：http或者https
                        .host(baseURL.host())//主机地址
                        .port(baseURL.port())//端口
                        .build();
                //获取处理后的新newRequest
                Request newRequest = builder.url(newHttpUrl).build();
                return chain.proceed(newRequest);
            } else {
                return chain.proceed(originalRequest);
            }

        }
    }

    /**
     * 日志拦截器
     * 将你访问的接口信息
     *
     * @return 拦截器
     */
    private HttpLoggingInterceptor getLoggerInterceptor() {
        //日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                String result = "";
                if (message.contains("HTTP FAILED")) {
                    result = message.substring(message.indexOf("HTTP FAILED"), message.length());
                }
                Intent intent = new Intent(Constants.MSG_SEND_EXCEPTION);
                intent.putExtra(Constants.MSG_EXCEPTION_NAME, result);
                activity.sendBroadcast(intent);
                LTGameOptions options = LTGameCommon.getInstance().options();
                if (options.isDebug()) {
                    Log.e("SDK_API_LOG", "--->" + message);
                }
            }
        });
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }
}
