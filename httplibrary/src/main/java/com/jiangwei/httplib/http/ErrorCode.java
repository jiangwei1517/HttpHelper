/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.jiangwei.httplib.http;

/**
 * Created by jiangwei18
 */
public enum  ErrorCode {
    /**
     * 由服务端控制错误文案的错误吗，用于客户端没有正确处理错误码情况的下补充，其info值由后端返回的errstr决定
     */
    SERVER_CONTROL_ERROR(99999, ""),
    DATA_IS_NULL(-3, "返回数据为空"),
    NETWORK_UNAVAILABLE(-2, "您网络暂时无法连接，请稍后重试"),
    UNKNOWN(-1, "操作失败，请重试"),
    SUCCESS(0, "请求成功"),
    PARAM_ERROR(1, "参数错误"),
    NETWORK_ERROR(2, "网络错误"),
    USER_NOT_LOGIN(200, "用户没有登录"),
    UPIMG_SIZE(6, "上传图片过大"),
    UPIMG_TYPE(7, "上传图片类型错误"),
    UPIMG_UPLOAD(8, "上传失败"),
    UPIMG_PIDENC(9, "系统错误"),
    UPIMG_PID(10, "系统错误"),
    VCODE_ERROR(12, "验证码错误"),
    SCORE_ERROR(14, "提高悬赏"),
    REPLY_ERROR(18, "回答失败"),
    UPDATE_QUESTION_ERROR(21, "问题补充失败"),

    UPUSERICON_SIZE(31, "上传头像过大"),
    UPUSERICON_TYPE(32, "上传头像类型错误"),
    UPUSERICON_UPLOAD(33, "上传头像失败"),

    CHARGE_ERROR(99, "未知错误"),
    TOKEN_ERROR(102, "token错误"),
    ANTISPAM_ERROR(101, "antispam验证错误"),
    //绑定手机错误
    PHONE_NUMBER_ERROR(109, "手机号码错误"),
    GET_VCODE_ERROR(111, "获取验证码错误"),
    BIND_PHONE_ERROR(110, "绑定失败"),
    PHONE_NUMBER_USED(112, "手机号已经被绑定过了"),
    VCODE_TIME_OUT(113, "验证码超时"),
    REPEAT_SUMBIT(105, "请勿重复提交!"),
    USER_NOTDOCTOR_ERROR(201, "用户不是医生"),
    USER_NOT_VALID_ERROR(202, "您没有答题医生的权限，具体问题可与管理员（QQ:2725326332）联系"),
    IMAGE_TYPE_ERROR(303, "请上传jpg,jpeg,png格式的图片"),

    API_EXEC_ERROR(400, "接口调用失败"),
    REPLY_REASK_ERROR(401, "有追问需要处理禁止当前操作"),
    FAMILY_DOCTOR_INVITE_ERROR(402, "家庭医生邀请错误"),
    STRING_TO_LONG(410, "提交文案过长"),

    ERROR_WITH_MESSAGE(800, ""),// 显示服务端返回的错误信息
    UNFREEZE_ERROR(1001, "忙碌时间无法解冻"),
    PARAM_NOT_EXIST(10002, "参数错误"),
    @Deprecated
    MOBILE_AND_EMAIL_NOR_EXIST(10003, "手机和邮箱为空"),
    SET_BLACK_LIST_ERROR(10004, "添加黑名单失败"),
    DEL_BLACK_LIST_ERROR(10005, "却笑黑名单失败"),
    EDIT_ANSWER_ERROR(10006, "编辑答案错误"),
    ACTION_CONF_ERROR(10007, "访问未知接口"),
    QUESTION_NOT_EXISTS(10008, "该问题已被删除，无法继续提交内容"),

    RESPONDER_ERROR(10009, "抢答失败"),
    SATISFACTION_ERROR(10010, "满意评价失败"),
    EVALUATE_ERROR(10011, "评价失败"),
    INVITE_EVALUATE_ERROR(10012, "邀请评价失败"),

    SEND_MSG_ERROR(10013, "消息发送失败"),
    DELETE_MSG_ERROR(10014, "删除消息失败"),

    ANSWER_SET_STATISFACTION(10015, "你的回答已经被评价为满意了"),
    ANSWER_SET_NOT_STATISFACTION(10016, "你的回答已经被评价为不满意了"),
    ANSWER_SET_GOOD(10007, "你的回答已经被评价为好评了"),
    ANSWER_SET_MIDDLER(10018, "你的回答已经被评价为中评了"),
    ANSWER_SET_BAD(10019, "你的回答已经被评价为差评了"),

    REPEAT_SUBMIT_ERROR(10039, "对不起，你刚提了个重复的问题，换个问题吧！"),
    EMAIL_DETECTED_ERROR(10040, "为了您的信息安全，请不要留下个人邮箱"),
    NEED_BIND_PHONE(10046, "需要绑定手机"),

    USER_WEALTH_NOR_ENOUGH(10020, "你的财富支不足"),
    SUBMIT_ERROR(10021, "提交失败"),
    SUBMIT_SIGN_ERROR(10023, "该版本已经不支持，升级最新的知道吧，更好的体验，更快的回答！"),
    QUESTION_IS_NOT_AVAILABLE(10041, "该问题已有其他的回答者，请试试其他问题吧T_T!"),

    REPEAT_SEND_GOOD(13001, "您已经赞过这个回答了"),
    QUESTION_IS_FROM_PC(10052, "很抱歉，该问题已解决，不再接收回答了"),
    QUESTION_HAS_BE_ANSWERED(10053, "很抱歉，该问题已解决，不再接收回答了"),
    QUESTION_HAS_BE_DELETED(10048, "抱歉，该问题可能违规已经关闭"),
    USER_IS_BLOCK(10055, "嘘，您被封禁了~"),
    YOU_ARE_IN_BLACK(10050, "嘘，您被封禁了~"),

    //反作弊签名验证失败
    ANTISPAM_REGISTERERR(51, "网络错误"),
    ANTISPAM_SIGNERR(52, "网络错误"),

    ALREADY_SING_IN(10126, "已签到"),
    ARTICLE_DELETED(10201, "该帖子已经被删除"),

    //领取任务奖励相关的返回码
    ERRNO_ACH_UNFINISH(10215, "成就未完成"),
    MISSION_NOT_FINISHED(10216, "任务未完成"),//任务未完成
    MISSION_RECEIVED(10217, "任务未完成"),
    ACHIEVEMENT_RECEIVED(10218, "任务未完成"),
    ERRNO_TASK_INVALID(10219, "任务失效，该奖励已经过期了~"),

    //客户端错误号, -10068 为客户端错误前缀,
    NET_EXCEPTION(-100680001, "网络连接不可用,请稍候重试"),
    PARSE_EXCEPTION(-100680002, "解析异常"),
    TIMEOUT_EXCEPTION(-100680003, "响应超时"),

    FAIL(-100680004, "操作失败"),
    CANCEL(-100680005, "取消操作"),
    APP_NOT_INSTALL(-100680006, "应用未安装"),
    APP_VERSION_TOO_LOW(-100670007, "应用版本太低"),
    APP_VERSION_TOO_HIGHT(-100680008, "应用版本太高"),

    APP_NOT_REGISTER(-100680009, "您的应用还未注册"),
    DATABASE_ERROR(-100680010, "数据库安装错误"),
    SERVICE_CONNECTION_ERROR(-100680011, "长连接服务错误"),
    FILE_IO_ERROR(-100680012, "文件存取错误"),
    DB_IO_ERROR(-100680013, "数据库存取错误");

    private final int errNo;
    private String info;

    private ErrorCode(int code, String errorInfo) {
        this.errNo = code;
        this.info = errorInfo;
    }

    public String toString() {
        return errNo + ":" + info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * 返回错误码
     *
     * @return
     */
    public int getErrorNo() {
        return errNo;
    }

    /**
     * 返回错误信息描述
     *
     * @return
     */
    public String getErrorInfo() {
        return info;
    }

    public static ErrorCode valueOf(int code) {
        ErrorCode[] values = values();
        for (ErrorCode error : values) {
            if (error.errNo == code) {
                return error;
            }
        }
        return UNKNOWN;
    }

    public static final int SERVER_CONTROL_ERROR_NO = 99999;
}
