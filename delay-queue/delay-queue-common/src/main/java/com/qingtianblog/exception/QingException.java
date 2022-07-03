package com.qingtianblog.exception;

import lombok.Getter;

/**
 * @author Guank
 * @version 1.0
 * @description: 异常父类
 * @date 2022/7/3 15:40
 */
@Getter
public class QingException extends RuntimeException {

    private final String code;
    private final String info;

    public QingException(ExceptionCode exceptionCode) {
        super(exceptionCode.getInfo());
        this.code = exceptionCode.getCode();
        this.info = exceptionCode.getInfo();
    }

    public QingException(String code, String info) {
        super(info);
        this.code = code;
        this.info = info;
    }
}