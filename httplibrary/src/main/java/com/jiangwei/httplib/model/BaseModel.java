/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.jiangwei.httplib.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by jiangwei18 on 2016/5/11.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class BaseModel<T> {

    private static long sLastRequestTimeStamp; // Seconds

    @JsonField(name = "errno")
    public int errorCode;
    @JsonField(name = "errmsg")
    public String errorInfo;
    @JsonField(name = "time")
    public long timeStamp;
    @JsonField(name = "data")
    public T result;

    public static void setLastRequestTimeStamp(long timeStamp) {
        sLastRequestTimeStamp = timeStamp;
        if (sLastRequestTimeStamp == 0) {
            sLastRequestTimeStamp = System.currentTimeMillis() / 1000;
        }
    }

    public static long getsLastRequestTimeStamp() {
        return sLastRequestTimeStamp;
    }
}
