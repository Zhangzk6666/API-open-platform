package top.zzkweb.sdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static top.zzkweb.sdk.utils.SignUtils.genSign;

/**
 * 调用API接口客户端
 */
@Slf4j
public class ApiClient {
    private static final String GATEWAY_HOST = "http://api-open-platform.zzkweb.top:8090";
    private final String accessKey;
    private final String secretKey;

    public ApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }


    /**
     * Hello
     */
    public String hello(String msg) {
        String path = GATEWAY_HOST + "/api/hello";
        String method = "GET";
        String message = "msg=" + msg;
//        String
        return this.baseRequest(path, method, message);
    }

    /**
     * 获取随机图片
     */
    public String getRandomPicture() {
        String path = GATEWAY_HOST + "/api/random_picture/get";
        String method = "GET";
        String message = "";
        return this.baseRequest(path, method, message);
    }

    public String baseRequest(String path, String method, String message) {
        if (method == null || StringUtils.isEmpty(method)) {
            throw new RuntimeException("接口请求必须是GET或者POST");
        }
        method = method.toUpperCase();
        HttpResponse httpResponse;
        if ("GET".equals(method)) {
            httpResponse = (HttpRequest.
                    get(path).
                    addHeaders(getHeaderMap(message)))
                    .body(message)
                    .execute();
        } else if ("POST".equals(method)) {
            httpResponse = (HttpRequest.
                    post(path).
                    addHeaders(getHeaderMap(message)))
                    .body(message)
                    .execute();
        } else {
            throw new RuntimeException("接口请求必须是get或者post");
        }
        log.info("request status: " + httpResponse.getStatus());
        return httpResponse.body();
    }


    /**
     * 添加请求头信息
     */
    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        // secretKey通过加密转成sign
        hashMap.put("sign", genSign(body, secretKey));
        System.out.println("getHeaderMap is: " + hashMap);
        return hashMap;
    }
}