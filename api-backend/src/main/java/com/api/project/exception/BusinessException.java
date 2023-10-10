package com.api.project.exception;

import com.api.project.common.ErrorCode;

import java.io.Serializable;

/**
 * 自定义异常类
 */
public class BusinessException extends RuntimeException implements Serializable {


    private static final long serialVersionUID = 5138161822138006146L;
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
