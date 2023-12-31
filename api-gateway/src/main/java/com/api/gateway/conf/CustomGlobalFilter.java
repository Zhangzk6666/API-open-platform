package com.api.gateway.conf;

import com.api.common.model.entity.InterfaceInfo;
import com.api.common.model.entity.User;
import com.api.common.service.InnerInterfaceInfoService;
import com.api.common.service.InnerUserInterfaceInfoService;
import com.api.common.service.InnerUserService;
import com.api.common.exception.RpcBusinessException;
import com.api.gateway.ratelimiter.annotation.RateConfigAnno;
import com.api.sdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * 全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

//    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    //    private static final String INTERFACE_HOST = "http://localhost:8123";
    @RateConfigAnno(limitType = "saveOrder", limitCount = 1)
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + path);
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getBody().toString());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());
        ServerHttpResponse response = exchange.getResponse();

//        // 2. 访问控制 - 黑白名单
//        if (!IP_WHITE_LIST.contains(sourceAddress)) {
//            response.setStatusCode(HttpStatus.FORBIDDEN);
//            log.error("黑白名单");
//            return response.setComplete();
//        }

        // 3. 用户鉴权（判断 ak、sk 是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        // 4. 去数据库中查是否已分配给用户
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null) {
            log.error("invokeUser == null");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            byte[] bytes = "用户身份验证错误!".getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return response.writeWith(Flux.just(buffer));
//            return handleNoAuth(response);
        }

        // 5. 验证apiClient的随机数nonce
        if (nonce != null && Long.parseLong(nonce) > 10000L) {
            log.error("nonce != null && Long.parseLong(nonce) > 10000L");

            return handleNoAuth(response);
        }

        // 6. 时间和当前时间不能超过 5 分钟
        long currentTime = System.currentTimeMillis() / 1000;
        final long FIVE_MINUTES = 60 * 5L;
        if (timestamp != null && (currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            log.error("6. 时间和当前时间不能超过 5 分钟");

            return handleNoAuth(response);
        }

        // 7. 从数据库中查出 secretKey
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.genSign(body, secretKey);
        if (sign == null || !sign.equals(serverSign)) {
            return handleNoAuth(response);
        }

        // 8. 查询数据库，模拟接口是否存在，以及请求方法是否匹配
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }
        log.info("过滤完成,准备处理响应,查询到的用户信息:" + invokeUser);
        log.info("过滤完成,准备处理响应,查询到的接口信息:" + interfaceInfo);

        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }

        // 是否还有调用次数
//        boolean stillTimesOfUse = innerInterfaceInfoService.isStillTimesOfUse(invokeUser.getId(), interfaceInfo.getId());
//        if (!stillTimesOfUse) {
//            response.setStatusCode(HttpStatus.FORBIDDEN);
//            byte[] bytes = "没有调用额度了".getBytes(StandardCharsets.UTF_8);
//            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
//            return response.writeWith(Flux.just(buffer));
//        }

        // 调用成功，接口调用次数 + 1 invokeCount
        try {
            boolean success = innerUserInterfaceInfoService.invokeCount(interfaceInfo.getId(), invokeUser.getId());

            if (!success) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                byte[] bytes = "没有调用额度了".getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                return response.writeWith(Flux.just(buffer));
            }
        } catch (RpcBusinessException e) {
            System.out.println(e);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            byte[] bytes = e.getMessage().getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return response.writeWith(Flux.just(buffer));
        } catch (Exception e) {
            log.error("innerUserInterfaceInfoService.invokeCount error: ", e);
            e.printStackTrace();
        }
        // 请求转发，调用模拟接口 + 响应日志
        // return chain.filter(exchange);
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());
    }

    /**
     * 处理响应
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(fluxBody.map(dataBuffer -> {
//                                 //调用成功，接口调用次数 + 1 invokeCount
//                                try {
//                                    boolean success = innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
//                                } catch (Exception e) {
//                                    log.error("invokeCount error", e);
//                                }
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                String data = new String(content, StandardCharsets.UTF_8); //data
                                sb2.append(data);
                                // 打印日志
                                log.info("响应结果：" + data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}