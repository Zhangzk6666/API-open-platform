package com.api.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zzk
 */
@RestController
public class Controller {
    @RequestMapping("/unauthorized_access")
    public void handleException(){
        System.out.println("====");
    }
}
