package com.api.common.exception;


import com.api.common.errcode.ErrorCode;

import java.io.Serializable;

/**
 * 自定义异常类
 */
public class RpcBusinessException extends RuntimeException implements Serializable {


    private static final long serialVersionUID = 5138161822138006146L;
    private int code;


    public RpcBusinessException() {
    }

    public RpcBusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public RpcBusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public RpcBusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
