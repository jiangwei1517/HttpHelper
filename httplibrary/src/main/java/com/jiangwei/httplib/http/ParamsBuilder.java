/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.jiangwei.httplib.http;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jiangwei18 on 2016/5/24.
 */
public class ParamsBuilder implements IParamsBuilder {

    private static AtomicInteger sCounter = new AtomicInteger(0);
    private static final String DEVICEID = "0";
    private static final String TOKEN = "1";
    private static final String VERSION_NAME = "2";
    private static final String SIGN = "3";
    private static final String COOKIE = "BDUSS = user valid in baidu";

    @Override
    public String getCookie() {
        return COOKIE;
    }

    @Override
    public String getDeviceId() {
        return DEVICEID;
    }

    @Override
    public String getVersionName() {
        return VERSION_NAME;
    }

    @Override
    public String getToken() {
        return TOKEN;
    }

    @Override
    public String getSign(String requestId, String path) {
        return SIGN;
    }

    public static String getRequestId() {
        StringBuilder sb = new StringBuilder();
        Random sRandom = new Random();
        int count = sCounter.getAndIncrement();
        if (count > 65535) {
            sCounter.set(0);
        }
        sb.append(System.currentTimeMillis()).append(sRandom.nextInt(10)).append(sRandom.nextInt(10))
                .append(count % 10);
        return sb.toString();
    }

    @Override
    public String getChannel() {
        return "Channel";
    }

}
