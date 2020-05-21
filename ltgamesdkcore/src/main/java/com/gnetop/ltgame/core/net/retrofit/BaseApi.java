package com.gnetop.ltgame.core.net.retrofit;


import android.app.Activity;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;


public interface BaseApi {

    Retrofit getRetrofit(Activity activity);

    OkHttpClient.Builder setInterceptor(Interceptor interceptor);

    Retrofit.Builder setConverterFactory(Converter.Factory factory);

}
