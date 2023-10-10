//package com.api.gateway;
//
//import cn.hutool.core.util.RandomUtil;
//import cn.hutool.http.HttpRequest;
//import cn.hutool.http.HttpResponse;
//import cn.hutool.json.JSONUtil;
//import com.api.common.model.entity.InterfaceInfo;
//import com.api.common.service.InnerInterfaceInfoService;
//import com.api.sdk.model.User;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static com.api.sdk.utils.SignUtils.genSign;
//
//@Slf4j
//@SpringBootTest
//public class ApiGatewayApplicationTest {
//
//    @DubboReference
//    private InnerInterfaceInfoService innerInterfaceInfoService;
//
//    @Test
//    public void invokeCount() {
//        // 8. 查询数据库，模拟接口是否存在，以及请求方法是否匹配
//        InterfaceInfo interfaceInfo = null;
//        String path = "http://localhost:8090/api/name/user";
//        String method = "POST";
//        try {
//            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
//        } catch (Exception e) {
//            log.error("getInterfaceInfo error", e);
//        }
//        if(interfaceInfo != null) System.out.println(interfaceInfo.toString());
//        else System.out.println("null");
//    }
//
//}
