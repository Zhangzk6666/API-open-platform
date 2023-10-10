package com.api.project.service;

import cn.hutool.http.HttpUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;

@SpringBootTest
public class UserInterfaceInfoServiceTest {
    private static final String GATEWAY_HOST = "http://106.52.109.24:8090";

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Test
    public void invokeCount() {
//        boolean b = userInterfaceInfoService.invokeCount(1L, 1L);
//        Assertions.assertTrue(b);
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("msg", "msg");
        System.out.println(GATEWAY_HOST + "/api/random_picture/get");
        String result = HttpUtil.get(GATEWAY_HOST + "/api/random_picture/get", paramMap);
        System.out.println("getPicture: " + result);
    }

}