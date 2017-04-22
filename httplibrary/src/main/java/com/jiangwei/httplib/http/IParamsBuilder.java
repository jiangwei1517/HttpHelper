/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.jiangwei.httplib.http;

/**
 * Created by jiangwei18 on 2016/5/13.
 */
public interface IParamsBuilder {
    String getCookie();

    String getDeviceId();

    String getVersionName();

    String getToken();

    String getSign(String requestId, String path);

    String getChannel();
}
