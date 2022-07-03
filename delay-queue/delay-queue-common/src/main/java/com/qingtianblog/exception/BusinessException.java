package com.qingtianblog.exception;

import lombok.Getter;

/**
 * @author Guank
 * @version 1.0
 * @description: 业务异常类
 * @date 2022/7/3 15:41
 */
@Getter
public class BusinessException extends QingException{
    public BusinessException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
