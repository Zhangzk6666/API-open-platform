package com.api.project.exception;

import com.api.project.common.BaseResponse;
import com.api.project.common.ErrorCode;
import com.api.project.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;


/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public BaseResponse<?> noHandlerFoundExceptionHandler(Exception e, HttpServletRequest request)  {
        log.error("FROM: {}, OTHERS: {}", request.getRequestURI(), e.getMessage());
        return ResultUtils.error(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public BaseResponse<?> httpRequestMethodNotSupportedExceptionHandler(Exception e, HttpServletRequest request) {
        log.error("FROM: {},  OTHERS: {}", request.getRequestURI(), e.getMessage());
        return ResultUtils.error(HttpStatus.METHOD_NOT_ALLOWED.value(), HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public BaseResponse<?>  exceptionHandler(Exception e, HttpServletRequest request) {
        log.error("FROM: {}, ERROR: {}", request.getRequestURI(), e.getMessage());
        return ResultUtils.error(500,"服务器异常");
    }


    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e, HttpServletRequest request) {
        log.error("FROM: {}, ERROR: {}", request.getRequestURI(), e.getMessage());
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e,HttpServletRequest request) {
        log.error("FROM: {}, ERROR: {}", request.getRequestURI(), e.getMessage());
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }

}

