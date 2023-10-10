//package com.api.project.service;
//
//import cn.hutool.core.util.RandomUtil;
//import cn.hutool.http.HttpRequest;
//import cn.hutool.http.HttpResponse;
//import cn.hutool.json.JSONUtil;
//import com.api.common.model.entity.InterfaceInfo;
//import com.api.project.common.ErrorCode;
//import com.api.project.common.ResultUtils;
//import com.api.project.exception.BusinessException;
//import com.api.project.model.enums.InterfaceInfoStatusEnum;
//import com.api.sdk.client.ApiClient;
//import com.api.sdk.model.User;
//import com.api.sdk.utils.SignUtils;
//import com.google.gson.Gson;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.annotation.Resource;
//import java.util.HashMap;
//import java.util.Map;
//
//@SpringBootTest
//public class PostUserInfoTest {
//
////    @Resource
////    private InterfaceInfoService interfaceInfoService;
//
//
////    @Test
//
//    //    public void rer(){
////
////        long id = 4;
////        String userRequestParams = "{\"username\":\"zzk\"}";
////        // 判断是否存在
////        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
////        if (oldInterfaceInfo == null) {
////            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
////        }
////        // 判断是否下线
////        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
////            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已下线");
////        }
////        // 拿自己的身份去操作
//////        User loginUser = userService.getLoginUser(request);
////        String accessKey = "1111111111111111";
////        String secretKey ="1111111111111111";
////        ApiClient client = new ApiClient(accessKey, secretKey);
////        // 把前端过来的userRequestParams转换成user.class
////        Gson gson = new Gson();
////        com.api.sdk.model.User user = gson.fromJson(userRequestParams, com.api.sdk.model.User.class);
////
////
////        String json = JSONUtil.toJsonStr(user);
////        System.out.println("===================="+json);
////        // 假设它就接入这个方法
////        String usernameByPost = getUsernameByPost(user);
////        System.out.println(usernameByPost);
////        System.out.println( ResultUtils.success(usernameByPost));
//////        return ResultUtils.success(usernameByPost);
////    }
//    static String accessKey = "1111111111111111";
//    static String secretKey = "1111111111111111";
//
//    public static void main(String[] args) {
//        String userRequestParams = "{\"username\":\"zzk\"}";
//
//        // 把前端过来的userRequestParams转换成user.class
//        Gson gson = new Gson();
//        com.api.sdk.model.User user = gson.fromJson(userRequestParams, com.api.sdk.model.User.class);
//
//        String json = JSONUtil.toJsonStr(user);
//        System.out.println("====================" + json);
//        // 假设它就接入这个方法
//        String usernameByPost = getUsernameByPost(user);
//        System.out.println(usernameByPost);
//        System.out.println(ResultUtils.success(usernameByPost));
////        return ResultUtils.success(usernameByPost);
//    }
//
//    public static String getUsernameByPost(User user) {
//        String json = JSONUtil.toJsonStr(user);
//        HttpResponse httpResponse = ((HttpRequest) HttpRequest.
//                get("http://localhost:8090/api/random_picture/get").
//                addHeaders(getHeaderMap(json))).body(json).execute();
////        log.info("getUsernameByPost " + httpResponse.getStatus());
//        String result = httpResponse.body();
////        log.info("getUsernameByPost " + result);
//        return result;
//    }
//
//    private static Map<String, String> getHeaderMap(String body) {
//        Map<String, String> hashMap = new HashMap();
//        hashMap.put("accessKey", accessKey);
//        hashMap.put("nonce", RandomUtil.randomNumbers(4));
//        hashMap.put("body", body);
//        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000L));
//        hashMap.put("sign", SignUtils.genSign(body, secretKey));
////        log.info("getHeaderMap is: " + hashMap);
//        return hashMap;
//    }
//}
