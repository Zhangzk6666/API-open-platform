package com.api.apiinterface.controller;


import com.api.apiinterface.common.ErrorCode;
import com.api.apiinterface.exception.BusinessException;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;


/**
 * 名称 API
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String getNameByGet(HttpServletRequest request) {
        String msg = request.getParameter("msg");
        if(msg == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不足");
        }
        return "hello,you msg is: "+msg;
    }
}
