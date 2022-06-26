package com.qingtianblog.exception;

/**
 * 统一异常码接口定义
 */

public interface ExceptionCode {

    /**
     * 获取异常编码
     *
     * @return 异常码
     */
    String getCode();

    /**
     * 获取异常信息
     *
     * @return 异常信息
     */
    String getInfo();
}
