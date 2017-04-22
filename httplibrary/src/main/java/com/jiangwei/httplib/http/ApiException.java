/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.jiangwei.httplib.http;

/**
 * Created by jiangwei18 on 2016/5/12.
 */
public class ApiException extends RuntimeException {

    private ErrorCode localErrCode;
    private int errCode;
    private String errMessage;

    public ApiException(ErrorCode errorCode) {
        this.localErrCode = errorCode;
        errCode = errorCode.getErrorNo();
        errMessage = errorCode.getErrorInfo();
    }

    public ApiException(ErrorCode errorCode, String errorMsg) {
        this.localErrCode = errorCode;
        errCode = errorCode.getErrorNo();
        errMessage = errorMsg;
    }


    public ErrorCode getLocalErrorCode() {
        return localErrCode;
    }

    public int getErrorCode() {
        return errCode;
    }

    public String getErrorMessage() {
        return errMessage;
    }
}
