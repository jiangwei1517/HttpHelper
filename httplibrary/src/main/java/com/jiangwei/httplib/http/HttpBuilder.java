
/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.jiangwei.httplib.http;


import com.github.aurae.retrofit2.LoganSquareConverterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import timber.log.Timber;


/**
 * Created by jiangwei18 on 2016/5/11.
 */
public class HttpBuilder {

    private static final int DEFAULT_TIMEOUT = 30;

    public interface BuildAction {
        void call(OkHttpClient.Builder builder);
    }

    // isReleased 判断是否为debug环境
    public static OkHttpClient createOkClient(boolean isReleased, BuildAction action) {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        if (action != null) {
            // 必须放在loggerInterceptor前面,才能获取到token,channel等信息
            action.call(okBuilder);
        }
        if (!isReleased) {
            try {
                Interceptor interceptor = (Interceptor) (Class.forName("com.facebook.stetho.okhttp3.StethoInterceptor").newInstance());
                okBuilder.addNetworkInterceptor(interceptor);
            } catch (Exception e) {
                Timber.tag("HttpBuilder").e(e, "Add StethoInterceptor error");
            }
            HttpLoggingInterceptor loggerInterceptor = new HttpLoggingInterceptor(
                    new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            Timber.tag("HttpLogging").i(message);
                        }
                    }
            );
            loggerInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okBuilder.addInterceptor(loggerInterceptor);
        }

        return okBuilder.build();
    }

    public static Retrofit createRetrofit(String baseUrl, OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(LoganSquareConverterFactory.create())
                .build();
        return retrofit;
    }
}
