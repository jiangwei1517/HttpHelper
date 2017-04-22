/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.jiangwei.httplib.http;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Created by jiangwei18 on 2016/5/11.
 */
public class ParamsInterceptor implements Interceptor {

    private static final String TAG = "ParamsInterceptor";

    private String deviceId;

    private IParamsBuilder mParamsBuilder;

    public ParamsInterceptor(@NonNull IParamsBuilder cookieBuilder) {
        mParamsBuilder = cookieBuilder;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
//        String path = request.url().url().getPath();
        String requestId = ParamsBuilder.getRequestId();
        HttpUrl.Builder requestBuilder = request.url().newBuilder();
        /*
         添加token, channel, deviceID, requestId,
         time:防止缓存
         requestId:防止dos攻击
          */
        buildCommonParams(requestBuilder, request, requestId);
        String params = requestBuilder.build().query();
        // 将body参数拼接在后面
        params = appendFormParams(request.body(), params);
        // 打印完整的请求信息
        Timber.tag(TAG).i("request params=%s", params);
        String sign = mParamsBuilder.getSign(requestId, params);
        // 签名
        requestBuilder.addQueryParameter("_s_", sign);
        Timber.tag(TAG).i("request sign=%s", sign);

        Request.Builder builder = request.newBuilder();
        builder.addHeader("Cookie", mParamsBuilder.getCookie());
        Request newRequest = builder.method(request.method(), request.body()).url(requestBuilder.build()).build();
        Response response = chain.proceed(newRequest);

        // Do not check and report error anymore
        checkResponse(response, request);

        return response;
    }

    private void buildCommonParams(HttpUrl.Builder requestBuilder, Request request, String requestId) {
        try {
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = URLEncoder.encode(mParamsBuilder.getDeviceId(), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            Log.w("RequestInterceptor", "encode error");
        }
        requestBuilder.scheme(request.url().scheme()).host(request.url().host())
                .addEncodedQueryParameter("_id_", deviceId).addQueryParameter("_tk_", mParamsBuilder.getToken())
                .addQueryParameter("_t_", String.valueOf(System.currentTimeMillis() / 1000))
                .addQueryParameter("_r_", requestId).addQueryParameter("_v_", mParamsBuilder.getVersionName())
                .addQueryParameter("_c_", mParamsBuilder.getChannel());
    }

    private String appendFormParams(RequestBody body, String params) {
        StringBuilder sb = new StringBuilder(params);
        if (body instanceof FormBody) {
            FormBody formBody = (FormBody) body;
            if (formBody.size() > 0) {
                sb.append("&");
                for (int i = 0; i < formBody.size(); i++) {
                    sb.append(formBody.encodedName(i)).append("=").append(formBody.encodedValue(i)).append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString();
    }

    private void checkResponse(Response response, Request request) {
        // Do not check audio binary bytes
        if (response.request().url().toString().contains("playaudio")) {
            return;
        }
        /*
         以application开头的媒体格式类型：

        application/xhtml+xml ：XHTML格式
        application/xml     ： XML数据格式
        application/atom+xml  ：Atom XML聚合格式
        application/json    ： JSON数据格式
        application/pdf       ：pdf格式
        application/msword  ： Word文档格式
        application/octet-stream ： 二进制流数据（如常见的文件下载）
        application/x-www-form-urlencoded ： <form encType=””>中默认的encType，form表单数据被编码为
         */
        MediaType contentType = response.body().contentType();
        if (contentType == null || !"application".equals(contentType.type()) || !"json".equals(contentType.subtype())) {
            StringBuilder builder = new StringBuilder("Invalid response found\nRequest:").append(request.url())
                    .append("\nResponse.Request:").append(response.request().url().toString())
                    .append("\nResponse.ContentType:").append(contentType.toString()).append("\nResponse.Body:\n");
            try {
                builder.append(response.body().string());
            } catch (IOException e) {
                Timber.tag("InvalidResponseException").w(e, "Failed to get the body");
            }
            Timber.wtf(new InvalidResponseException(builder.toString()));
        }
    }

    private static class InvalidResponseException extends Exception {
        public InvalidResponseException(String detailMessage) {
            super(detailMessage);
        }
    }
}
