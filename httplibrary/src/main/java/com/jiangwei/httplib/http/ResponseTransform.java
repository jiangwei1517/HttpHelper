/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.jiangwei.httplib.http;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.ParameterizedType;
import com.jiangwei.httplib.model.BaseModel;

import org.json.JSONObject;

import rx.android.BuildConfig;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Created by jiangwei18 on 2016/5/11.
 */
public class ResponseTransform<T> implements Func1<BaseModel<T>, T> {

    public static final ResponseTransform sTransform = new ResponseTransform();

    @Override
    public T call(BaseModel<T> baseModel) {
        if (BuildConfig.DEBUG) {
            try {
                String json = new JSONObject(LoganSquare.serialize(baseModel, new ParameterizedType<BaseModel<T>>() {
                })).toString(4);
                Timber.tag("Response").i(json);
            } catch (Exception e) {
                Timber.tag("ResponseTransform").w(e, "Error when serialize %s", baseModel.toString());
            }
            // 判断服务器返回的errorCode,抛出ApiException给onError处理
            if (baseModel.errorCode != 0) {
                ErrorCode errorCode = ErrorCode.valueOf(baseModel.errorCode);
                if (errorCode == ErrorCode.ERROR_WITH_MESSAGE) {
                    // 服务器返回错误800
                    throw new ApiException(errorCode, baseModel.errorInfo);
                } else {
                    throw new ApiException(errorCode);
                }
            }
        }
        return baseModel.result;
    }

}
