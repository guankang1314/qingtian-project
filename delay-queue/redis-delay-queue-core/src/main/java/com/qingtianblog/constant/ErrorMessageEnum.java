package com.qingtianblog.constant;


import com.qingtianblog.exception.ExceptionCode;

/**
 * 错误码  100XX
 *
 */
public enum ErrorMessageEnum implements ExceptionCode {

    ACQUIRE_LOCK_FAIL("10010", "获取分布式锁失败"),
    JOB_ALREADY_EXIST("10020", "JOB信息已存在"),

    ;

    private final String code;

    private final String info;

    ErrorMessageEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

    /**
     * 获取异常编码
     *
     * @return 异常码
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * 获取异常信息
     *
     * @return 异常信息
     */
    @Override
    public String getInfo() {
        return info;
    }
}
